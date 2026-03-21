package com.powsybl.dynawo.psse.dyr;

import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Index of all {@link DyrRecord} instances parsed from a PSS/E {@code .dyr} file,
 * organised as an immutable map whose key is the PSS/E model name (e.g.
 * {@code "GENROU"}, {@code "TGOV1"}) and whose value is the ordered list of
 * every component record registered under that model name.
 *
 * <h2>Building the store</h2>
 * <pre>{@code
 * // From a file
 * DyrModelStore store = DyrModelStore.of(Path.of("network.dyr"));
 *
 * // From an already-parsed record list
 * List<DyrRecord> records = new DyrParser().parseContent(rawText);
 * DyrModelStore store = DyrModelStore.of(records);
 * }</pre>
 *
 * <h2>Querying the store</h2>
 * <pre>{@code
 * // All GENROU records
 * List<DyrRecord> genrous = store.get("GENROU");
 *
 * // Typed retrieval
 * List<GenrouRecord> genrous = store.get("GENROU", GenrouRecord.class);
 *
 * // All records for a specific bus
 * List<DyrRecord> busModels = store.getByBus(101);
 *
 * // Check presence
 * boolean hasPss = store.contains("PSS2A");
 *
 * // Iterate every model/record pair
 * store.forEach((modelName, recordList) -> ...);
 * }</pre>
 *
 * <h2>Thread safety</h2>
 * The store is fully immutable after construction and therefore safe for
 * concurrent access without synchronisation.
 *
 * @author powsybl-dynawo contributors
 */
public final class DyrModelStore {

    /**
     * Ordered map: modelName → unmodifiable list of records for that model.
     * Insertion order is preserved (records appear in the order they were parsed).
     */
    private final Map<String, List<DyrRecord>> index;

    /** Total number of records across all model types. */
    private final int totalRecords;

    // -------------------------------------------------------------------------
    // Construction
    // -------------------------------------------------------------------------

    private DyrModelStore(Map<String, List<DyrRecord>> index) {
        this.index = index;
        this.totalRecords = index.values().stream().mapToInt(List::size).sum();
    }

    /**
     * Builds a {@code DyrModelStore} from an already-parsed list of records.
     *
     * @param records list produced by {@link DyrParser#parseContent} or {@link DyrParser#parse}
     * @return new store instance
     */
    public static DyrModelStore of(List<DyrRecord> records) {
        // Accumulate into a mutable linked map to preserve encounter order per model
        Map<String, List<DyrRecord>> mutable = new LinkedHashMap<>();
        for (DyrRecord r : records) {
            mutable.computeIfAbsent(r.modelName(), k -> new ArrayList<>()).add(r);
        }
        // Freeze every value list
        Map<String, List<DyrRecord>> frozen = new LinkedHashMap<>(mutable.size());
        mutable.forEach((model, list) -> frozen.put(model, Collections.unmodifiableList(list)));
        return new DyrModelStore(Collections.unmodifiableMap(frozen));
    }

    /**
     * Parses the given {@code .dyr} file and builds the store in one step.
     *
     * @param path path to the PSS/E dyr file
     * @return new store instance
     * @throws IOException if the file cannot be read
     */
    public static DyrModelStore of(Path path) throws IOException {
        return of(new DyrParser().parse(path));
    }

    /**
     * Parses raw dyr text and builds the store in one step.
     *
     * @param content raw text of a {@code .dyr} file
     * @return new store instance
     */
    public static DyrModelStore ofContent(String content) {
        return of(new DyrParser().parseContent(content));
    }

    // -------------------------------------------------------------------------
    // Core access
    // -------------------------------------------------------------------------

    /**
     * Returns the unmodifiable list of all records whose model name equals
     * {@code modelName} (case-insensitive). Returns an empty list when the
     * model is absent — never {@code null}.
     *
     * @param modelName PSS/E model keyword, e.g. {@code "GENROU"} or {@code "genrou"}
     * @return ordered, unmodifiable list of matching records (possibly empty)
     */
    public List<DyrRecord> get(String modelName) {
        return index.getOrDefault(normalise(modelName), List.of());
    }

    /**
     * Returns the records for {@code modelName} cast to {@code type}.
     *
     * <p>This is a convenience wrapper for the common pattern
     * {@code store.get("GENROU", GenrouRecord.class)} that avoids an
     * unchecked cast at the call site.
     *
     * @param modelName PSS/E model keyword
     * @param type      concrete record class
     * @param <T>       record type
     * @return unmodifiable list of records cast to {@code T}
     * @throws ClassCastException if any record stored under {@code modelName}
     *                            is not an instance of {@code type}
     */
    public <T extends DyrRecord> List<T> get(String modelName, Class<T> type) {
        return get(modelName).stream()
                .map(type::cast)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the first record for {@code modelName} wrapped in an
     * {@link Optional}, or {@link Optional#empty()} when no such record exists.
     * Useful for models that are expected to appear at most once per file.
     *
     * @param modelName PSS/E model keyword
     * @return optional first record
     */
    public Optional<DyrRecord> getFirst(String modelName) {
        List<DyrRecord> list = get(modelName);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    /**
     * Returns the first record for {@code modelName} cast to {@code type},
     * wrapped in an {@link Optional}.
     *
     * @param modelName PSS/E model keyword
     * @param type      concrete record class
     * @param <T>       record type
     * @return optional first record cast to {@code T}
     */
    public <T extends DyrRecord> Optional<T> getFirst(String modelName, Class<T> type) {
        return getFirst(modelName).map(type::cast);
    }

    // -------------------------------------------------------------------------
    // Bus-level access
    // -------------------------------------------------------------------------

    /**
     * Returns all records (across all model types) whose {@code busNumber}
     * equals {@code bus}. The records are returned in parse order.
     *
     * @param bus bus number
     * @return unmodifiable list of records attached to {@code bus}
     */
    public List<DyrRecord> getByBus(int bus) {
        return index.values().stream()
                .flatMap(List::stream)
                .filter(r -> r.busNumber() == bus)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns all records for a specific bus and model name combination.
     * In a well-formed dyr file there is usually at most one, but PSS/E
     * allows multiple machines per bus with distinct ids.
     *
     * @param bus       bus number
     * @param modelName PSS/E model keyword
     * @return unmodifiable list of matching records
     */
    public List<DyrRecord> getByBusAndModel(int bus, String modelName) {
        return get(modelName).stream()
                .filter(r -> r.busNumber() == bus)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Returns the record for a specific bus, model name, and machine id,
     * wrapped in an {@link Optional}.
     *
     * @param bus       bus number
     * @param modelName PSS/E model keyword
     * @param machineId machine / device id
     * @return optional matching record
     */
    public Optional<DyrRecord> getByBusModelAndId(int bus, String modelName, String machineId) {
        return get(modelName).stream()
                .filter(r -> r.busNumber() == bus && r.machineId().equals(machineId))
                .findFirst();
    }

    // -------------------------------------------------------------------------
    // Introspection
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if at least one record with the given model name
     * exists in this store.
     *
     * @param modelName PSS/E model keyword
     * @return {@code true} if the model is present
     */
    public boolean contains(String modelName) {
        return index.containsKey(normalise(modelName));
    }

    /**
     * Returns the set of all model names present in this store, in the order
     * their first record was encountered during parsing.
     *
     * @return unmodifiable, ordered set of model name strings
     */
    public Set<String> modelNames() {
        return index.keySet(); // already unmodifiable (unmodifiableMap wraps it)
    }

    /**
     * Returns the number of records stored under {@code modelName}.
     * Equivalent to {@code get(modelName).size()} but avoids list allocation.
     *
     * @param modelName PSS/E model keyword
     * @return count of records for that model (0 if absent)
     */
    public int count(String modelName) {
        return get(modelName).size();
    }

    /**
     * Returns the total number of records in the store across all model types.
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
     * <p>Keys are uppercase model names; values are unmodifiable lists of
     * records in parse order. The map itself preserves insertion order.
     *
     * @return unmodifiable map view
     */
    public Map<String, List<DyrRecord>> asMap() {
        return index;
    }

    /**
     * Iterates every (modelName, recordList) pair in parse-encounter order.
     *
     * @param action bi-consumer receiving the model name and its record list
     */
    public void forEach(java.util.function.BiConsumer<String, List<DyrRecord>> action) {
        index.forEach(action);
    }

    /**
     * Returns a flat, unmodifiable list of every record in this store, in the
     * order they were originally parsed (i.e. the same list that was passed to
     * {@link #of(List)}).
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
        StringBuilder sb = new StringBuilder("DyrModelStore{totalRecords=");
        sb.append(totalRecords).append(", models=[");
        index.forEach((model, list) ->
                sb.append(model).append('(').append(list.size()).append("), "));
        if (totalRecords > 0) {
            sb.setLength(sb.length() - 2); // trim trailing ", "
        }
        sb.append("]}");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static String normalise(String modelName) {
        return modelName == null ? "" : modelName.toUpperCase(java.util.Locale.ROOT).trim();
    }
}
