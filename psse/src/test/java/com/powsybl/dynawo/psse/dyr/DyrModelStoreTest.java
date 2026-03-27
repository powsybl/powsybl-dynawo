package com.powsybl.dynawo.psse.dyr;

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
 * Unit tests for {@link DyrModelStore}.
 */
class DyrModelStoreTest {

    // -------------------------------------------------------------------------
    // Shared fixture
    // -------------------------------------------------------------------------

    /**
     * A dyr snippet that contains:
     *  – bus 101: GENROU '1', SEXS '1', TGOV1 '1', STAB1 '1'
     *  – bus 102: GENCLS '1', SEXS '1'
     *  – bus 103: GENCLS '1', GENCLS '2'   ← two machines on same bus
     */
    private static final String FIXTURE = """
            @ bus 101 full generator block
            101 'GENROU' '1'  8.00  0.03  0.40  0.05  6.50  0.0  1.80  1.72  0.30  0.50  0.25  0.20  0.07  0.13 /
            101 'SEXS'   '1'  0.1  10.0  100.0  0.05  -3.0  3.0 /
            101 'TGOV1'  '1'  0.05  0.5  1.0  0.0  2.1  7.0  0.0 /
            101 'STAB1'  '1'  20.0  1.5  0.15  0.05  0.15  0.05  0.05 /
            @ bus 102 simple machine
            102 'GENCLS' '1'  6.5  0.0 /
            102 'SEXS'   '1'  0.1  10.0  100.0  0.05  -3.0  3.0 /
            @ bus 103 two machines
            103 'GENCLS' '1'  4.0  0.0 /
            103 'GENCLS' '2'  3.5  0.0 /
            """;

    private DyrModelStore store;

    @BeforeEach
    void setUp() {
        store = DyrModelStore.ofContent(FIXTURE);
    }

    // =========================================================================
    // Factory method tests
    // =========================================================================

    @Nested
    @DisplayName("Factory methods")
    class FactoryTests {

        @Test
        @DisplayName("of(List) builds store from pre-parsed records")
        void ofList() {
            List<DyrRecord> records = new DyrParser().parseContent(FIXTURE);
            DyrModelStore s = DyrModelStore.of(records);
            assertThat(s.totalCount()).isEqualTo(store.totalCount());
            assertThat(s.modelNames()).isEqualTo(store.modelNames());
        }

        @Test
        @DisplayName("of(Path) reads file and produces same store as ofContent")
        void ofPath(@TempDir Path tmpDir) throws IOException {
            Path file = tmpDir.resolve("test.dyr");
            Files.writeString(file, FIXTURE);
            DyrModelStore s = DyrModelStore.of(file);
            assertThat(s.totalCount()).isEqualTo(store.totalCount());
            assertThat(s.modelNames()).isEqualTo(store.modelNames());
        }

        @Test
        @DisplayName("of(Path) throws IOException for missing file")
        void ofPathMissing(@TempDir Path tmpDir) {
            assertThatThrownBy(() -> DyrModelStore.of(tmpDir.resolve("nope.dyr")))
                    .isInstanceOf(IOException.class);
        }

        @Test
        @DisplayName("ofContent with empty string produces empty store")
        void ofContentEmpty() {
            DyrModelStore s = DyrModelStore.ofContent("");
            assertThat(s.isEmpty()).isTrue();
            assertThat(s.totalCount()).isZero();
            assertThat(s.modelNames()).isEmpty();
        }

        @Test
        @DisplayName("of(empty list) produces empty store")
        void ofEmptyList() {
            DyrModelStore s = DyrModelStore.of(List.of());
            assertThat(s.isEmpty()).isTrue();
        }
    }

    // =========================================================================
    // Core get() tests
    // =========================================================================

    @Nested
    @DisplayName("get(modelName)")
    class GetByModelTests {

        @Test
        @DisplayName("Returns correct count for model present multiple times")
        void getMultipleRecords() {
            List<DyrRecord> gencls = store.get("GENCLS");
            assertThat(gencls).hasSize(3); // bus 102, 103×2
        }

        @Test
        @DisplayName("Returns single record for model present once")
        void getSingleRecord() {
            assertThat(store.get("TGOV1")).hasSize(1);
        }

        @Test
        @DisplayName("Returns empty list (not null) for absent model")
        void getAbsentModel() {
            List<DyrRecord> result = store.get("NONEXISTENT");
            assertThat(result).isNotNull().isEmpty();
        }

        @Test
        @DisplayName("Returns same result regardless of case (case-insensitive)")
        void getCaseInsensitive() {
            assertThat(store.get("genrou")).hasSize(store.get("GENROU").size());
            assertThat(store.get("Gencls")).hasSize(store.get("GENCLS").size());
        }

        @Test
        @DisplayName("Returned list is unmodifiable")
        void getListIsUnmodifiable() {
            List<DyrRecord> list = store.get("GENCLS");
            assertThatThrownBy(() -> list.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("All records for a model have that modelName set")
        void allRecordsHaveCorrectModelName() {
            store.get("GENCLS").forEach(r ->
                    assertThat(r.modelName()).isEqualTo("GENCLS"));
        }
    }

    // =========================================================================
    // Typed get(modelName, Class) tests
    // =========================================================================

    @Nested
    @DisplayName("get(modelName, Class<T>)")
    class TypedGetTests {

        @Test
        @DisplayName("Returns correctly typed list without cast at call site")
        void typedGetReturnsList() {
            List<GenclsRecord> records = store.get("GENCLS", GenclsRecord.class);
            assertThat(records).hasSize(3);
            records.forEach(r -> assertThat(r).isInstanceOf(GenclsRecord.class));
        }

        @Test
        @DisplayName("Typed get returns empty list for absent model")
        void typedGetAbsent() {
            List<Tgov1Record> records = store.get("HYGOV", Tgov1Record.class);
            assertThat(records).isEmpty();
        }

        @Test
        @DisplayName("Typed get allows accessing model-specific fields directly")
        void typedGetFieldAccess() {
            List<GenrouRecord> genrous = store.get("GENROU", GenrouRecord.class);
            assertThat(genrous).hasSize(1);
            GenrouRecord g = genrous.getFirst();
            assertThat(g.h()).isCloseTo(6.50, within(1e-9));
            assertThat(g.xd()).isCloseTo(1.80, within(1e-9));
        }

        @Test
        @DisplayName("Typed get throws ClassCastException for wrong type")
        void typedGetWrongTypeThrows() {
            // GENROU records are stored under "GENROU", requesting them as GenclsRecord must fail
            assertThatThrownBy(() -> store.get("GENROU", GenclsRecord.class))
                    .isInstanceOf(ClassCastException.class);
        }
    }

    // =========================================================================
    // getFirst() tests
    // =========================================================================

    @Nested
    @DisplayName("getFirst()")
    class GetFirstTests {

        @Test
        @DisplayName("getFirst returns Optional with first record for present model")
        void getFirstPresent() {
            Optional<DyrRecord> first = store.getFirst("GENCLS");
            assertThat(first).isPresent();
            assertThat(first.get().busNumber()).isEqualTo(102); // first GENCLS in parse order
        }

        @Test
        @DisplayName("getFirst returns empty Optional for absent model")
        void getFirstAbsent() {
            assertThat(store.getFirst("NONEXISTENT")).isEmpty();
        }

        @Test
        @DisplayName("getFirst(modelName, Class) returns typed Optional")
        void getFirstTyped() {
            Optional<GenclsRecord> first = store.getFirst("GENCLS", GenclsRecord.class);
            assertThat(first).isPresent();
            assertThat(first.get().h()).isCloseTo(6.5, within(1e-9));
        }

        @Test
        @DisplayName("getFirst(modelName, Class) returns empty Optional for absent model")
        void getFirstTypedAbsent() {
            Optional<Stab1Record> result = store.getFirst("IEEEG1", Stab1Record.class);
            assertThat(result).isEmpty();
        }
    }

    // =========================================================================
    // Bus-level access tests
    // =========================================================================

    @Nested
    @DisplayName("getByBus() and getByBusAndModel()")
    class BusLevelTests {

        @Test
        @DisplayName("getByBus returns all records for a bus across all model types")
        void getByBusAllModels() {
            List<DyrRecord> bus101 = store.getByBus(101);
            assertThat(bus101).hasSize(4); // GENROU + SEXS + TGOV1 + STAB1
        }

        @Test
        @DisplayName("getByBus for bus with two machines returns all of them")
        void getByBusTwoMachines() {
            List<DyrRecord> bus103 = store.getByBus(103);
            assertThat(bus103).hasSize(2);
        }

        @Test
        @DisplayName("getByBus returns empty list for unknown bus")
        void getByBusUnknown() {
            assertThat(store.getByBus(9999)).isEmpty();
        }

        @Test
        @DisplayName("getByBusAndModel returns only records matching both bus and model")
        void getByBusAndModel() {
            List<DyrRecord> result = store.getByBusAndModel(102, "SEXS");
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().busNumber()).isEqualTo(102);
            assertThat(result.getFirst().modelName()).isEqualTo("SEXS");
        }

        @Test
        @DisplayName("getByBusAndModel returns empty when model exists but not at requested bus")
        void getByBusAndModelMismatchedBus() {
            assertThat(store.getByBusAndModel(103, "TGOV1")).isEmpty();
        }

        @Test
        @DisplayName("getByBusModelAndId pinpoints a single record by bus + model + machineId")
        void getByBusModelAndId() {
            Optional<DyrRecord> result = store.getByBusModelAndId(103, "GENCLS", "2");
            assertThat(result).isPresent();
            assertThat(result.get().busNumber()).isEqualTo(103);
            assertThat(result.get().machineId()).isEqualTo("2");
            GenclsRecord g = (GenclsRecord) result.get();
            assertThat(g.h()).isCloseTo(3.5, within(1e-9));
        }

        @Test
        @DisplayName("getByBusModelAndId returns empty for unknown machineId")
        void getByBusModelAndIdUnknown() {
            assertThat(store.getByBusModelAndId(103, "GENCLS", "99")).isEmpty();
        }
    }

    // =========================================================================
    // Introspection tests
    // =========================================================================

    @Nested
    @DisplayName("Introspection: contains(), modelNames(), count(), totalCount()")
    class IntrospectionTests {

        @Test
        @DisplayName("contains returns true for present model (case-insensitive)")
        void containsPresent() {
            assertThat(store.contains("GENROU")).isTrue();
            assertThat(store.contains("genrou")).isTrue();
            assertThat(store.contains("Tgov1")).isTrue();
        }

        @Test
        @DisplayName("contains returns false for absent model")
        void containsAbsent() {
            assertThat(store.contains("HYGOV")).isFalse();
            assertThat(store.contains("")).isFalse();
        }

        @Test
        @DisplayName("modelNames returns all distinct model names present")
        void modelNames() {
            Set<String> names = store.modelNames();
            assertThat(names).containsExactlyInAnyOrder("GENROU", "SEXS", "TGOV1", "STAB1", "GENCLS");
        }

        @Test
        @DisplayName("modelNames set is unmodifiable")
        void modelNamesUnmodifiable() {
            Set<String> names = store.modelNames();
            assertThatThrownBy(() -> names.add("FAKE"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("count returns correct record count per model")
        void countPerModel() {
            assertThat(store.count("GENCLS")).isEqualTo(3);
            assertThat(store.count("SEXS")).isEqualTo(2);
            assertThat(store.count("TGOV1")).isEqualTo(1);
        }

        @Test
        @DisplayName("count returns 0 for absent model (not present)")
        void countAbsent() {
            assertThat(store.count("PSS2A")).isZero();
        }

        @Test
        @DisplayName("totalCount equals sum of all individual counts")
        void totalCount() {
            int summed = store.modelNames().stream().mapToInt(store::count).sum();
            assertThat(store.totalCount()).isEqualTo(summed);
        }

        @Test
        @DisplayName("totalCount equals number of records originally parsed")
        void totalCountMatchesParsedRecords() {
            int parsed = new DyrParser().parseContent(FIXTURE).size();
            assertThat(store.totalCount()).isEqualTo(parsed);
        }

        @Test
        @DisplayName("isEmpty returns false for non-empty store")
        void isNotEmpty() {
            assertThat(store.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("isEmpty returns true for empty store")
        void isEmpty() {
            assertThat(DyrModelStore.of(List.of()).isEmpty()).isTrue();
        }
    }

    // =========================================================================
    // Iteration and view tests
    // =========================================================================

    @Nested
    @DisplayName("Iteration: asMap(), forEach(), allRecords()")
    class IterationTests {

        @Test
        @DisplayName("asMap returns unmodifiable map with same content as direct get()")
        void asMapContent() {
            Map<String, List<DyrRecord>> map = store.asMap();
            assertThat(map).containsKey("GENCLS");
            assertThat(map.get("GENCLS")).hasSize(3);
        }

        @Test
        @DisplayName("asMap is unmodifiable at the map level")
        void asMapUnmodifiable() {
            assertThatThrownBy(() -> store.asMap().put("FAKE", List.of()))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("asMap values are unmodifiable lists")
        void asMapValuesUnmodifiable() {
            List<DyrRecord> list = store.asMap().get("GENCLS");
            assertThatThrownBy(() -> list.add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("forEach iterates every model exactly once")
        void forEachIteratesAllModels() {
            AtomicInteger iterations = new AtomicInteger();
            List<String> visitedModels = new ArrayList<>();
            store.forEach((model, list) -> {
                iterations.incrementAndGet();
                visitedModels.add(model);
            });
            assertThat(iterations.get()).isEqualTo(store.modelNames().size());
            assertThat(visitedModels).containsExactlyInAnyOrderElementsOf(store.modelNames());
        }

        @Test
        @DisplayName("forEach total record count across all lists equals totalCount")
        void forEachTotalRecordCount() {
            AtomicInteger total = new AtomicInteger();
            store.forEach((model, list) -> total.addAndGet(list.size()));
            assertThat(total.get()).isEqualTo(store.totalCount());
        }

        @Test
        @DisplayName("allRecords returns flat list with all records in parse order")
        void allRecordsReturnsFlatList() {
            List<DyrRecord> all = store.allRecords();
            assertThat(all).hasSize(store.totalCount());
        }

        @Test
        @DisplayName("allRecords list is unmodifiable")
        void allRecordsUnmodifiable() {
            assertThatThrownBy(() -> store.allRecords().add(null))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Model name insertion order is preserved (encounter order)")
        void insertionOrderPreserved() {
            // Fixture parse order: GENROU, SEXS, TGOV1, STAB1, GENCLS
            List<String> orderedNames = new ArrayList<>(store.modelNames());
            assertThat(orderedNames).containsExactly("GENROU", "SEXS", "TGOV1", "STAB1", "GENCLS");
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
        void toStringContainsTotalCount() {
            String s = store.toString();
            assertThat(s).contains("totalRecords=" + store.totalCount());
        }

        @Test
        @DisplayName("toString includes each model name")
        void toStringContainsModelNames() {
            String s = store.toString();
            store.modelNames().forEach(model -> assertThat(s).contains(model));
        }

        @Test
        @DisplayName("toString of empty store does not throw")
        void toStringEmptyStore() {
            assertThatCode(() -> DyrModelStore.of(List.of()).toString())
                    .doesNotThrowAnyException();
        }
    }

    // =========================================================================
    // Edge case / boundary tests
    // =========================================================================

    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("get(null) does not throw – returns empty list")
        void getNullModelName() {
            assertThatCode(() -> store.get(null))
                    .doesNotThrowAnyException();
            assertThat(store.get(null)).isEmpty();
        }

        @Test
        @DisplayName("contains(null) returns false")
        void containsNull() {
            assertThat(store.contains(null)).isFalse();
        }

        @Test
        @DisplayName("Single-record store has correct model name, count and content")
        void singleRecordStore() {
            DyrModelStore s = DyrModelStore.ofContent("101 'GENCLS' '1'  6.5  0.0 /\n");
            assertThat(s.totalCount()).isEqualTo(1);
            assertThat(s.modelNames()).containsExactly("GENCLS");
            assertThat(s.count("GENCLS")).isEqualTo(1);
            assertThat(s.isEmpty()).isFalse();
        }

        @Test
        @DisplayName("All records in the same model group share the same modelName value")
        void modelNameConsistencyWithinGroup() {
            store.asMap().forEach((expectedModel, list) ->
                    list.forEach(r -> assertThat(r.modelName()).isEqualTo(expectedModel)));
        }

        @Test
        @DisplayName("Modifying the source list passed to of(List) does not affect the store")
        void sourceListMutationDoesNotAffectStore() {
            List<DyrRecord> mutableList = new ArrayList<>(new DyrParser().parseContent(FIXTURE));
            DyrModelStore s = DyrModelStore.of(mutableList);
            int originalCount = s.totalCount();
            mutableList.clear(); // mutate source after building store
            assertThat(s.totalCount()).isEqualTo(originalCount);
        }
    }
}
