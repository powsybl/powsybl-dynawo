package com.powsybl.dynawo.psse.dyr;

import com.powsybl.dynawo.psse.dyr.exciters.SexsRecord;
import com.powsybl.dynawo.psse.dyr.generators.GenclsRecord;
import com.powsybl.dynawo.psse.dyr.generators.GenrouRecord;
import com.powsybl.dynawo.psse.dyr.governors.Tgov1Record;
import com.powsybl.dynawo.psse.dyr.stabilizers.Stab1Record;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for {@link DyrIdStore}.
 *
 * <p>Fixture layout:
 * <pre>
 *   bus 101  id '1'  → GENROU, SEXS, TGOV1, STAB1
 *   bus 102  id '1'  → GENCLS, SEXS
 *   bus 103  id '1'  → GENCLS
 *   bus 103  id '2'  → GENCLS
 * </pre>
 * So the id index contains:
 * <ul>
 *   <li>id {@code "1"} → 7 records (GENROU, SEXS, TGOV1, STAB1 from bus 101;
 *       GENCLS, SEXS from bus 102; GENCLS from bus 103)</li>
 *   <li>id {@code "2"} → 1 record  (GENCLS from bus 103)</li>
 * </ul>
 */
class DyrIdStoreTest {

    private static final String FIXTURE = """
            @ bus 101 full generator block, all id '1'
            101 'GENROU' '1'  8.00  0.03  0.40  0.05  6.50  0.0  1.80  1.72  0.30  0.50  0.25  0.20  0.07  0.13 /
            101 'SEXS'   '1'  0.1  10.0  100.0  0.05  -3.0  3.0 /
            101 'TGOV1'  '1'  0.05  0.5  1.0  0.0  2.1  7.0  0.0 /
            101 'STAB1'  '1'  20.0  1.5  0.15  0.05  0.15  0.05  0.05 /
            @ bus 102 simple machine, id '1'
            102 'GENCLS' '1'  6.5  0.0 /
            102 'SEXS'   '1'  0.1  10.0  100.0  0.05  -3.0  3.0 /
            @ bus 103 two machines, ids '1' and '2'
            103 'GENCLS' '1'  4.0  0.0 /
            103 'GENCLS' '2'  3.5  0.0 /
            """;

    private DyrIdStore store;

    @BeforeEach
    void setUp() {
        store = DyrIdStore.ofContent(FIXTURE);
    }

    // =========================================================================
    // Factory method tests
    // =========================================================================

    @Nested
    @DisplayName("Factory methods")
    class FactoryTests {

        @Test
        @DisplayName("ofContent builds store with correct id keys")
        void ofContent() {
            assertThat(store.ids()).containsExactlyInAnyOrder("1", "2");
        }

        @Test
        @DisplayName("of(List) produces same store as ofContent")
        void ofList() {
            List<DyrRecord> records = new DyrParser().parseContent(FIXTURE);
            DyrIdStore s = DyrIdStore.of(records);
            assertThat(s.ids()).isEqualTo(store.ids());
            assertThat(s.totalCount()).isEqualTo(store.totalCount());
        }

        @Test
        @DisplayName("of(Path) reads file and produces same result as ofContent")
        void ofPath(@TempDir Path tmpDir) throws IOException {
            Path file = tmpDir.resolve("test.dyr");
            Files.writeString(file, FIXTURE);
            DyrIdStore s = DyrIdStore.of(file);
            assertThat(s.ids()).isEqualTo(store.ids());
            assertThat(s.totalCount()).isEqualTo(store.totalCount());
        }

        @Test
        @DisplayName("of(Path) throws IOException for missing file")
        void ofPathMissing(@TempDir Path tmpDir) {
            assertThatThrownBy(() -> DyrIdStore.of(tmpDir.resolve("missing.dyr")))
                    .isInstanceOf(IOException.class);
        }

        @Test
        @DisplayName("of(DyrModelStore) reuses already-parsed records")
        void ofModelStore() {
            DyrModelStore modelStore = DyrModelStore.ofContent(FIXTURE);
            DyrIdStore s = DyrIdStore.of(modelStore);
            assertThat(s.ids()).isEqualTo(store.ids());
            assertThat(s.totalCount()).isEqualTo(store.totalCount());
        }

        @Test
        @DisplayName("ofContent with empty string produces empty store")
        void ofContentEmpty() {
            DyrIdStore s = DyrIdStore.ofContent("");
            assertThat(s.isEmpty()).isTrue();
            assertThat(s.ids()).isEmpty();
            assertThat(s.totalCount()).isZero();
        }

        @Test
        @DisplayName("of(empty list) produces empty store")
        void ofEmptyList() {
            assertThat(DyrIdStore.of(List.of()).isEmpty()).isTrue();
        }
    }

    // =========================================================================
    // Core get(id) tests
    // =========================================================================

    @Nested
    @DisplayName("get(id)")
    class GetByIdTests {

        @Test
        @DisplayName("id '1' groups records from all buses that use that id")
        void getIdOneCrossesMultipleBuses() {
            List<DyrRecord> id1 = store.get("1");
            // 4 (bus 101) + 2 (bus 102) + 1 (bus 103 id '1') = 7
            assertThat(id1).hasSize(7);
        }

        @Test
        @DisplayName("id '2' contains only the single machine at bus 103")
        void getIdTwo() {
            List<DyrRecord> id2 = store.get("2");
            assertThat(id2).hasSize(1);
            assertThat(id2.getFirst().busNumber()).isEqualTo(103);
            assertThat(id2.getFirst().modelName()).isEqualTo("GENCLS");
        }

        @Test
        @DisplayName("get for absent id returns empty list, not null")
        void getAbsentIdReturnsEmpty() {
            List<DyrRecord> result = store.get("99");
            assertThat(result).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("get(null) returns empty list without throwing")
        void getNullReturnsEmpty() {
            assertThatCode(() -> store.get(null)).doesNotThrowAnyException();
            assertThat(store.get(null)).isEmpty();
        }

        @Test
        @DisplayName("Returned list is unmodifiable")
        void returnedListIsUnmodifiable() {
            List<DyrRecord> list = store.get("1");
            assertThatThrownBy(() -> list.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("All records in the list share the expected machineId value")
        void allRecordsShareMachineId() {
            store.get("1").forEach(r -> assertThat(r.machineId()).isEqualTo("1"));
            store.get("2").forEach(r -> assertThat(r.machineId()).isEqualTo("2"));
        }

        @Test
        @DisplayName("Records for id '1' span all expected model types")
        void id1SpansMultipleModelTypes() {
            List<DyrRecord> id1 = store.get("1");
            assertThat(id1).anyMatch(r -> r instanceof GenrouRecord)
                           .anyMatch(r -> r instanceof SexsRecord)
                           .anyMatch(r -> r instanceof Tgov1Record)
                           .anyMatch(r -> r instanceof Stab1Record)
                           .anyMatch(r -> r instanceof GenclsRecord);
        }
    }

    // =========================================================================
    // Typed get(id, Class) tests
    // =========================================================================

    @Nested
    @DisplayName("get(id, Class<T>)")
    class TypedGetTests {

        /**
         * A fixture where id '1' maps to a single model type — the only safe
         * scenario for typed get(id, Class) on DyrIdStore.
         * Bus 201: GENCLS '1' only.
         */
        private static final String SINGLE_MODEL_FIXTURE =
                "201 'GENCLS' '1'  6.5  0.0 /\n" +
                        "202 'GENCLS' '1'  3.0  0.5 /\n";

        @Test
        @DisplayName("Typed get works correctly when all records for an id share the same model type")
        void typedGetHomogeneousId() {
            DyrIdStore s = DyrIdStore.ofContent(SINGLE_MODEL_FIXTURE);
            List<GenclsRecord> result = s.get("1", GenclsRecord.class);
            assertThat(result).hasSize(2);
            result.forEach(r -> assertThat(r).isInstanceOf(GenclsRecord.class));
        }

        @Test
        @DisplayName("Typed get allows direct field access without cast for homogeneous id")
        void typedGetFieldAccess() {
            DyrIdStore s = DyrIdStore.ofContent(SINGLE_MODEL_FIXTURE);
            List<GenclsRecord> result = s.get("1", GenclsRecord.class);
            assertThat(result).hasSize(2);
            assertThat(result.get(0).h()).isCloseTo(6.5, within(1e-9));
            assertThat(result.get(1).h()).isCloseTo(3.0, within(1e-9));
        }

        @Test
        @DisplayName("Typed get returns empty list for absent id")
        void typedGetAbsentId() {
            List<GenrouRecord> result = store.get("99", GenrouRecord.class);
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Typed get throws ClassCastException when id contains mixed model types")
        void typedGetMixedTypesThrows() {
            // id '1' in the main fixture contains GENROU + SEXS + TGOV1 + STAB1 + GENCLS
            // — attempting to cast all to a single type fails on the second record
            assertThatThrownBy(() -> store.get("1", GenrouRecord.class))
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("Use getByIdAndModel for type-safe access when id maps to mixed model types")
        void typedAccessViaGetByIdAndModel() {
            // Correct approach for mixed-type ids: filter by model name first, then cast
            List<SexsRecord> sexsRecords = store.get("1", SexsRecord.class,
                    r -> r.modelName().equals("SEXS"));
            assertThat(sexsRecords).hasSize(2);
            sexsRecords.forEach(r -> assertThat(r.busNumber()).isIn(101, 102));
        }
    }

    // =========================================================================
    // getFirst() tests
    // =========================================================================

    @Nested
    @DisplayName("getFirst()")
    class GetFirstTests {

        @Test
        @DisplayName("getFirst returns Optional with first record in parse order")
        void getFirstPresent() {
            Optional<DyrRecord> first = store.getFirst("1");
            assertThat(first).isPresent();
            // First record for id '1' in parse order is GENROU at bus 101
            assertThat(first.get()).isInstanceOf(GenrouRecord.class);
            assertThat(first.get().busNumber()).isEqualTo(101);
        }

        @Test
        @DisplayName("getFirst returns empty Optional for absent id")
        void getFirstAbsent() {
            assertThat(store.getFirst("99")).isEmpty();
        }

        @Test
        @DisplayName("getFirst(id, Class) returns typed Optional")
        void getFirstTyped() {
            Optional<GenrouRecord> first = store.getFirst("1", GenrouRecord.class);
            assertThat(first).isPresent();
            assertThat(first.get().busNumber()).isEqualTo(101);
        }

        @Test
        @DisplayName("getFirst(id, Class) throws ClassCastException if first record is wrong type")
        void getFirstTypedWrongType() {
            // First record for id '1' is GENROU, not GENCLS
            assertThatThrownBy(() -> store.getFirst("1", GenclsRecord.class))
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("getFirst(id, Class) returns empty Optional for absent id")
        void getFirstTypedAbsent() {
            assertThat(store.getFirst("99", GenclsRecord.class)).isEmpty();
        }
    }

    // =========================================================================
    // Compound-key access tests
    // =========================================================================

    @Nested
    @DisplayName("getByBusAndId(), getByBusIdAndModel(), getByIdAndModel()")
    class CompoundKeyTests {

        @Test
        @DisplayName("getByBusAndId returns all records for a specific bus + id pair")
        void getByBusAndId() {
            List<DyrRecord> result = store.getByBusAndId(101, "1");
            // bus 101 id '1': GENROU, SEXS, TGOV1, STAB1
            assertThat(result).hasSize(4);
            result.forEach(r -> {
                assertThat(r.busNumber()).isEqualTo(101);
                assertThat(r.machineId()).isEqualTo("1");
            });
        }

        @Test
        @DisplayName("getByBusAndId returns only records matching bus (not other buses with same id)")
        void getByBusAndIdFiltersOtherBuses() {
            List<DyrRecord> result = store.getByBusAndId(102, "1");
            assertThat(result).hasSize(2); // only bus 102 records, not bus 101 or 103
            result.forEach(r -> assertThat(r.busNumber()).isEqualTo(102));
        }

        @Test
        @DisplayName("getByBusAndId returns empty list when bus has no records for that id")
        void getByBusAndIdNoMatch() {
            assertThat(store.getByBusAndId(101, "2")).isEmpty();
        }

        @Test
        @DisplayName("getByBusIdAndModel pinpoints single record by bus + id + modelName")
        void getByBusIdAndModel() {
            Optional<DyrRecord> result = store.getByBusIdAndModel(101, "1", "TGOV1");
            assertThat(result).isPresent();
            assertThat(result.get()).isInstanceOf(Tgov1Record.class);
            assertThat(result.get().busNumber()).isEqualTo(101);
        }

        @Test
        @DisplayName("getByBusIdAndModel is case-insensitive on model name")
        void getByBusIdAndModelCaseInsensitive() {
            Optional<DyrRecord> upper = store.getByBusIdAndModel(101, "1", "TGOV1");
            Optional<DyrRecord> lower = store.getByBusIdAndModel(101, "1", "tgov1");
            assertThat(upper).isPresent();
            assertThat(lower).isPresent();
            assertThat(upper.get().busNumber()).isEqualTo(lower.get().busNumber());
        }

        @Test
        @DisplayName("getByBusIdAndModel returns empty when model not present at that bus/id")
        void getByBusIdAndModelNoMatch() {
            // bus 102 id '1' has no TGOV1
            assertThat(store.getByBusIdAndModel(102, "1", "TGOV1")).isEmpty();
        }

        @Test
        @DisplayName("getByBusIdAndModel returns empty for null model name")
        void getByBusIdAndModelNullModel() {
            assertThat(store.getByBusIdAndModel(101, "1", null)).isEmpty();
        }

        @Test
        @DisplayName("getByIdAndModel returns all records with given id+model across all buses")
        void getByIdAndModel() {
            // SEXS with id '1' appears at bus 101 and bus 102
            List<DyrRecord> result = store.getByIdAndModel("1", "SEXS");
            assertThat(result).hasSize(2);
            assertThat(result).extracting(DyrRecord::modelName).containsOnly("SEXS");
            assertThat(result).extracting(DyrRecord::busNumber)
                              .containsExactlyInAnyOrder(101, 102);
        }

        @Test
        @DisplayName("getByIdAndModel returns empty when model absent for that id")
        void getByIdAndModelNoMatch() {
            assertThat(store.getByIdAndModel("2", "SEXS")).isEmpty();
        }

        @Test
        @DisplayName("getByIdAndModel is case-insensitive on model name")
        void getByIdAndModelCaseInsensitive() {
            assertThat(store.getByIdAndModel("1", "sexs"))
                    .hasSameSizeAs(store.getByIdAndModel("1", "SEXS"));
        }
    }

    // =========================================================================
    // Introspection tests
    // =========================================================================

    @Nested
    @DisplayName("Introspection: contains(), ids(), count(), totalCount(), isEmpty()")
    class IntrospectionTests {

        @Test
        @DisplayName("contains returns true for present id")
        void containsPresent() {
            assertThat(store.contains("1")).isTrue();
            assertThat(store.contains("2")).isTrue();
        }

        @Test
        @DisplayName("contains returns false for absent id")
        void containsAbsent() {
            assertThat(store.contains("99")).isFalse();
        }

        @Test
        @DisplayName("contains returns false for null")
        void containsNull() {
            assertThat(store.contains(null)).isFalse();
        }

        @Test
        @DisplayName("ids() returns all distinct machine ids present")
        void ids() {
            Set<String> ids = store.ids();
            assertThat(ids).containsExactlyInAnyOrder("1", "2");
        }

        @Test
        @DisplayName("ids() set is unmodifiable")
        void idsUnmodifiable() {
            assertThatThrownBy(() -> store.ids().add("99"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("count returns correct number of records per id")
        void countPerIdCorrect() {
            assertThat(store.count("1")).isEqualTo(7);
            assertThat(store.count("2")).isEqualTo(1);
        }

        @Test
        @DisplayName("count returns 0 for absent id")
        void countAbsentId() {
            assertThat(store.count("99")).isZero();
        }

        @Test
        @DisplayName("totalCount equals sum of all per-id counts")
        void totalCountEqualsSumOfCounts() {
            int summed = store.ids().stream().mapToInt(store::count).sum();
            assertThat(store.totalCount()).isEqualTo(summed);
        }

        @Test
        @DisplayName("totalCount equals number of originally parsed records")
        void totalCountMatchesParsed() {
            int parsed = new DyrParser().parseContent(FIXTURE).size();
            assertThat(store.totalCount()).isEqualTo(parsed);
        }

        @Test
        @DisplayName("isEmpty returns false for non-empty store")
        void isNotEmpty() {
            assertThat(store.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("isEmpty returns true for store built from empty content")
        void isEmptyTrue() {
            assertThat(DyrIdStore.ofContent("").isEmpty()).isTrue();
        }
    }

    // =========================================================================
    // Iteration and view tests
    // =========================================================================

    @Nested
    @DisplayName("Iteration: asMap(), forEach(), allRecords()")
    class IterationTests {

        @Test
        @DisplayName("asMap has correct keys and list sizes")
        void asMapContent() {
            Map<String, List<DyrRecord>> map = store.asMap();
            assertThat(map).containsKey("1").containsKey("2");
            assertThat(map.get("1")).hasSize(7);
            assertThat(map.get("2")).hasSize(1);
        }

        @Test
        @DisplayName("asMap is unmodifiable at map level")
        void asMapUnmodifiable() {
            assertThatThrownBy(() -> store.asMap().put("99", List.of()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("asMap value lists are unmodifiable")
        void asMapValueListsUnmodifiable() {
            assertThatThrownBy(() -> store.asMap().get("1").add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("forEach iterates every id exactly once")
        void forEachVisitsAllIds() {
            AtomicInteger iterations = new AtomicInteger();
            List<String> visitedIds = new ArrayList<>();
            store.forEach((id, list) -> {
                iterations.incrementAndGet();
                visitedIds.add(id);
            });
            assertThat(iterations.get()).isEqualTo(store.ids().size());
            assertThat(visitedIds).containsExactlyInAnyOrderElementsOf(store.ids());
        }

        @Test
        @DisplayName("forEach total record count matches totalCount()")
        void forEachTotalRecordCount() {
            AtomicInteger total = new AtomicInteger();
            store.forEach((id, list) -> total.addAndGet(list.size()));
            assertThat(total.get()).isEqualTo(store.totalCount());
        }

        @Test
        @DisplayName("allRecords returns flat list with every record")
        void allRecordsFlatList() {
            assertThat(store.allRecords()).hasSize(store.totalCount());
        }

        @Test
        @DisplayName("allRecords list is unmodifiable")
        void allRecordsUnmodifiable() {
            assertThatThrownBy(() -> store.allRecords().add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Insertion order of ids is preserved (encounter order)")
        void insertionOrderPreserved() {
            // Fixture encounter order: '1' appears first (bus 101), then '2' (bus 103)
            List<String> orderedIds = new ArrayList<>(store.ids());
            assertThat(orderedIds).containsExactly("1", "2");
        }
    }

    // =========================================================================
    // toString tests
    // =========================================================================

    @Nested
    @DisplayName("toString()")
    class ToStringTests {

        @Test
        @DisplayName("toString includes total record count")
        void toStringTotalCount() {
            assertThat(store.toString()).contains("totalRecords=" + store.totalCount());
        }

        @Test
        @DisplayName("toString includes each id")
        void toStringContainsIds() {
            String s = store.toString();
            store.ids().forEach(id -> assertThat(s).contains(id));
        }

        @Test
        @DisplayName("toString of empty store does not throw")
        void toStringEmpty() {
            assertThatCode(() -> DyrIdStore.of(List.of()).toString())
                    .doesNotThrowAnyException();
        }
    }

    // =========================================================================
    // Consistency with DyrModelStore tests
    // =========================================================================

    @Nested
    @DisplayName("Consistency with DyrModelStore")
    class ConsistencyTests {

        @Test
        @DisplayName("Both stores built from the same content hold the same total record count")
        void totalCountConsistent() {
            DyrModelStore modelStore = DyrModelStore.ofContent(FIXTURE);
            assertThat(store.totalCount()).isEqualTo(modelStore.totalCount());
        }

        @Test
        @DisplayName("allRecords lists from both stores contain the same records (unordered)")
        void allRecordsContainSameElements() {
            DyrModelStore modelStore = DyrModelStore.ofContent(FIXTURE);
            assertThat(store.allRecords())
                    .containsExactlyInAnyOrderElementsOf(modelStore.allRecords());
        }

        @Test
        @DisplayName("DyrIdStore.of(DyrModelStore) and DyrIdStore.ofContent produce identical stores")
        void ofModelStoreEquivalentToOfContent() {
            DyrModelStore modelStore = DyrModelStore.ofContent(FIXTURE);
            DyrIdStore fromModelStore = DyrIdStore.of(modelStore);
            assertThat(fromModelStore.ids()).isEqualTo(store.ids());
            assertThat(fromModelStore.totalCount()).isEqualTo(store.totalCount());
            fromModelStore.ids().forEach(id ->
                    assertThat(fromModelStore.count(id)).isEqualTo(store.count(id)));
        }

        @Test
        @DisplayName("Modifying the source list passed to of(List) does not affect the store")
        void sourceListMutationDoesNotAffectStore() {
            List<DyrRecord> mutableList = new ArrayList<>(new DyrParser().parseContent(FIXTURE));
            DyrIdStore s = DyrIdStore.of(mutableList);
            int originalCount = s.totalCount();
            mutableList.clear();
            assertThat(s.totalCount()).isEqualTo(originalCount);
        }
    }
}
