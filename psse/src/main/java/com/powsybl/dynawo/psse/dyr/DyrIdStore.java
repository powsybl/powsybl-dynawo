package com.powsybl.dynawo.psse.dyr;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Index of all {@link DyrRecord} instances parsed from a PSS/E {@code .dyr} file,
 * organised as an immutable map whose key is the <b>machine / device id</b>
 * (e.g. {@code "1"}, {@code "G1"}) and whose value is the ordered list of every
 * component record registered under that id.
 *
 * <p>In PSS/E a machine id is a short string (often just {@code "1"}) that
 * distinguishes multiple dynamic models attached to the same bus.  The same id
 * value can legally appear on different buses; this store groups records by id
 * regardless of bus, which is useful when you need to retrieve all dynamic
 * models belonging to a particular machine across the entire network.
 *
 * <h2>Building the store</h2>
 * <pre>{@code
 * // From a file
 * DyrIdStore store = DyrIdStore.of(Path.of("network.dyr"));
 *
 * // From an already-parsed record list
 * List<DyrRecord> records = new DyrParser().parseContent(rawText);
 * DyrIdStore store = DyrIdStore.of(records);
 *
 * // Directly from a DyrModelStore (avoids re-parsing)
 * DyrIdStore store = DyrIdStore.of(modelStore);
 * }</pre>
 *
 * <h2>Querying the store</h2>
 * <pre>{@code
 * // All records whose machineId equals "1"
 * List<DyrRecord> all = store.get("1");
 *
 * // Typed retrieval
 * List<GenrouRecord> genrous = store.get("1", GenrouRecord.class);
 *
 * // First record for this id
 * Optional<DyrRecord> first = store.getFirst("1");
 *
 * // Narrow by bus + id
 * List<DyrRecord> bus101id1 = store.getByBusAndId(101, "1");
 *
 * // Pinpoint by bus + id + modelName
 * Optional<DyrRecord> exact = store.getByBusIdAndModel(101, "1", "GENROU");
 *
 * // Iterate all id / record-list pairs
 * store.forEach((id, list) -> ...);
 * }</pre>
 *
 * <h2>Relationship to {@link DyrModelStore}</h2>
 * Both stores are views over the same underlying records and can be built from
 * the same parsed list. They complement each other:
 * <ul>
 *   <li>{@link DyrModelStore} – "give me all GENROU machines"</li>
 *   <li>{@code DyrIdStore}    – "give me all models for machine id '1'"</li>
 * </ul>
 *
 * <h2>Thread safety</h2>
 * The store is fully immutable after construction and therefore safe for
 * concurrent access without synchronisation.
 *
 * @author powsybl-dynawo contributors
 */
public final class DyrIdStore {

    /**
     * Ordered map: machineId → unmodifiable list of records for that id.
     * Insertion order reflects the order ids were first encountered during parsing.
     */
    private final Map<String, List<DyrRecord>> index;

    /** Total number of records across all ids. */
    private final int totalRecords;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    private DyrIdStore(Map<String, List<DyrRecord>> index) {
        this.index = index;
        this.totalRecords = index.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Builds a {@code DyrIdStore} from an already-parsed list of records.
     *
     * @param records list produced by {@link DyrParser#parseContent} or {@link DyrParser#parse}
     * @return new store instance
     */
    public static DyrIdStore of(List<DyrRecord> records) {
        Map<String, List<DyrRecord>> mutable = new LinkedHashMap<>();
        for (DyrRecord r : records) {
            mutable.computeIfAbsent(r.machineId(), k -> new ArrayList<>()).add(r);
        }
        Map<String, List<DyrRecord>> frozen = new LinkedHashMap<>(mutable.size());
        mutable.forEach((id, list) -> frozen.put(id, Collections.unmodifiableList(list)));
        return new DyrIdStore(Collections.unmodifiableMap(frozen));
    }

    /**
     * Parses the given {@code .dyr} file and builds the store in one step.
     *
     * @param path path to the PSS/E dyr file
     * @return new store instance
     * @throws IOException if the file cannot be read
     */
    public static DyrIdStore of(Path path) throws IOException {
        return of(new DyrParser().parse(path));
    }

    /**
     * Parses raw dyr text and builds the store in one step.
     *
     * @param content raw text of a {@code .dyr} file
     * @return new store instance
     */
    public static DyrIdStore ofContent(String content) {
        return of(new DyrParser().parseContent(content));
    }

    /**
     * Builds a {@code DyrIdStore} from an existing {@link DyrModelStore},
     * reusing the already-parsed records without re-parsing the file.
     *
     * @param modelStore source model store
     * @return new store instance
     */
    public static DyrIdStore of(DyrModelStore modelStore) {
        return of(modelStore.allRecords());
    }

    // -------------------------------------------------------------------------
    // Core access
    // -------------------------------------------------------------------------

    /**
     * Returns the unmodifiable list of all records whose {@code machineId}
     * equals {@code id}. Returns an empty list when the id is absent — never
     * {@code null}.
     *
     * @param id machine / device id (exact match, case-sensitive)
     * @return ordered, unmodifiable list of matching records (possibly empty)
     */
    public List<DyrRecord> get(String id) {
        if (id == null) {
            return List.of();
        }
        return index.getOrDefault(id, List.of());
    }

    /**
     * Returns the records for {@code id} cast to {@code type}.
     *
     * <p><b>Only safe when all records stored under {@code id} share the same
     * model type.</b> If the id maps to mixed model types (the common case in
     * real dyr files where id {@code "1"} groups generators, exciters,
     * governors, etc.) this method throws {@link ClassCastException} on the
     * first non-matching record.
     *
     * <p>For mixed-type ids prefer the filtered overload
     * {@link #get(String, Class, java.util.function.Predicate)} or
     * {@link #getByIdAndModel(String, String)}.
     *
     * @param id   machine / device id
     * @param type concrete record class
     * @param <T>  record type
     * @return unmodifiable typed list
     * @throws ClassCastException if any record stored under {@code id} is not
     *                            an instance of {@code type}
     */
    public <T extends DyrRecord> List<T> get(String id, Class<T> type) {
        return get(id).stream()
                .map(type::cast)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns only the records for {@code id} that satisfy {@code filter},
     * cast to {@code type}.
     *
     * <p>This is the safe alternative to {@link #get(String, Class)} when
     * {@code id} maps to records of mixed model types. Filter on model name
     * before casting:
     * <pre>{@code
     * List<SexsRecord> sexs = store.get("1", SexsRecord.class,
     *         r -> r.modelName().equals("SEXS"));
     * }</pre>
     *
     * @param id     machine / device id
     * @param type   concrete record class
     * @param filter predicate applied before casting; only matching records
     *               are included in the result
     * @param <T>    record type
     * @return unmodifiable filtered and typed list
     */
    public <T extends DyrRecord> List<T> get(String id, Class<T> type,
                                             java.util.function.Predicate<DyrRecord> filter) {
        return get(id).stream()
                .filter(filter)
                .map(type::cast)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the first record for {@code id} wrapped in an {@link Optional},
     * or {@link Optional#empty()} when no such record exists.
     *
     * @param id machine / device id
     * @return optional first record
     */
    public Optional<DyrRecord> getFirst(String id) {
        List<DyrRecord> list = get(id);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * Returns the first record for {@code id} cast to {@code type}, wrapped
     * in an {@link Optional}.
     *
     * @param id   machine / device id
     * @param type concrete record class
     * @param <T>  record type
     * @return optional first record cast to {@code T}
     */
    public <T extends DyrRecord> Optional<T> getFirst(String id, Class<T> type) {
        return getFirst(id).map(type::cast);
    }

    // -------------------------------------------------------------------------
    // Compound-key access
    // -------------------------------------------------------------------------

    /**
     * Returns all records matching both {@code bus} and {@code id}.
     * In a well-formed dyr file there is usually at most one record per model
     * type for a given (bus, id) pair, but the method returns a list to handle
     * edge cases and multi-model queries.
     *
     * @param bus bus number
     * @param id  machine / device id
     * @return unmodifiable list of matching records
     */
    public List<DyrRecord> getByBusAndId(int bus, String id) {
        return get(id).stream()
                .filter(r -> r.busNumber() == bus)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the unique record identified by the triple (bus, id, modelName),
     * wrapped in an {@link Optional}.
     *
     * <p>This is the most precise lookup available: in a valid PSS/E dyr file
     * at most one record can exist for a given (bus, id, modelName) combination.
     *
     * @param bus       bus number
     * @param id        machine / device id
     * @param modelName PSS/E model keyword (case-insensitive)
     * @return optional matching record
     */
    public Optional<DyrRecord> getByBusIdAndModel(int bus, String id, String modelName) {
        String normalised = modelName == null ? "" : modelName.toUpperCase(Locale.ROOT).trim();
        return get(id).stream()
                .filter(r -> r.busNumber() == bus && r.modelName().equals(normalised))
                .findFirst();
    }

    /**
     * Returns all records for a given {@code id} that belong to the specified
     * {@code modelName}.
     *
     * <p>Useful for cross-checking: e.g. "find every machine with id '1' that
     * has a TGOV1 governor across the whole network."
     *
     * @param id        machine / device id
     * @param modelName PSS/E model keyword (case-insensitive)
     * @return unmodifiable list of matching records
     */
    public List<DyrRecord> getByIdAndModel(String id, String modelName) {
        String normalised = modelName == null ? "" : modelName.toUpperCase(Locale.ROOT).trim();
        return get(id).stream()
                .filter(r -> r.modelName().equals(normalised))
                .collect(Collectors.toUnmodifiableList());
    }

    // -------------------------------------------------------------------------
    // Introspection
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if at least one record with the given machine id
     * exists in this store.
     *
     * @param id machine / device id (case-sensitive)
     * @return {@code true} if the id is present
     */
    public boolean contains(String id) {
        return id != null && index.containsKey(id);
    }

    /**
     * Returns the set of all machine ids present in this store, in the order
     * their first record was encountered during parsing.
     *
     * @return unmodifiable, ordered set of machine id strings
     */
    public Set<String> ids() {
        return index.keySet();
    }

    /**
     * Returns the number of records stored under {@code id}.
     *
     * @param id machine / device id
     * @return count (0 if absent)
     */
    public int count(String id) {
        return get(id).size();
    }

    /**
     * Returns the total number of records in the store across all ids.
     *
     * @return total record count
     */
    public int totalCount() {
        return totalRecords;
    }

    /**
     * Returns {@code true} if the store contains no records at all.
     *
     * @return {@code true} when the store is empty
     */
    public boolean isEmpty() {
        return totalRecords == 0;
    }

    // -------------------------------------------------------------------------
    // Iteration and view
    // -------------------------------------------------------------------------

    /**
     * Returns an unmodifiable view of the underlying
     * {@code Map<String, List<DyrRecord>>}.
     *
     * <p>Keys are machine ids in encounter order; values are unmodifiable lists
     * of records in parse order.
     *
     * @return unmodifiable map view
     */
    public Map<String, List<DyrRecord>> asMap() {
        return index;
    }

    /**
     * Iterates every (id, recordList) pair in parse-encounter order.
     *
     * @param action bi-consumer receiving the machine id and its record list
     */
    public void forEach(BiConsumer<String, List<DyrRecord>> action) {
        index.forEach(action);
    }

    /**
     * Returns a flat, unmodifiable list of every record in this store in the
     * order they were originally parsed.
     *
     * @return all records in parse order
     */
    public List<DyrRecord> allRecords() {
        return index.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    // -------------------------------------------------------------------------
    // Object overrides
    // -------------------------------------------------------------------------

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("DyrIdStore{totalRecords=");
        sb.append(totalRecords).append(", ids=[");
        index.forEach((id, list) ->
                sb.append('"').append(id).append('"')
                  .append('(').append(list.size()).append("), "));
        if (totalRecords > 0) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]}");
        return sb.toString();
    }
}
