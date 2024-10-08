<template>
  <div>
    <div class="w100 space-1x">
      <div class="space-x1 flex">
        <p class="bold space-x1 w25">
          Predicates:
        </p>
        <p class="bold space-x1 w10">
          Start:
        </p>
        <p class="bold space-x1 w10">
          End:
        </p>
        <div class="w50">
          <button class="border space-x1 hover-background hover-pointer" @click="addPredicate()">
            Add Predicate
          </button>
        </div>
      </div>
      <div v-for="predicate in predicates.values()" class="w100 flex">
        <div class="w25 space-x1">
          <p @click="editPredicate(predicate.id)" class="unselected hover-pointer">
            {{ predicate.id }}:
          </p>
        </div>
        <div class="w10 space-x1">
          <input type="checkbox" v-if="start.value.includes(predicate.id)" checked="checked"
                 @click="toggleStart(predicate.id); validatePredicate(actions,predicates,predicate,predicate.id,start.value,end)">
          <input type="checkbox" v-else @click="toggleStart(predicate.id); validatePredicate(actions,predicates,predicate,predicate.id,start.value,end)">
        </div>
        <div class="w10 space-x1">
          <input type="checkbox" v-if="end.includes(predicate.id)" checked="checked"
                 @click="toggleEnd(predicate.id); validatePredicate(actions,predicates,predicate,predicate.id,start.value,end)">
          <input type="checkbox" v-else @click="toggleEnd(predicate.id); validatePredicate(actions,predicates,predicate,predicate.id,start.value,end)">
        </div>
        <div class="w50 space-x1">
          <p v-if="predicate.valid" class="valid">
            {{ predicate.result }}
          </p>
          <p v-else class="invalid">
            {{ predicate.result }}
          </p>
        </div>
      </div>
    </div>
    <div class="w100 space-1x">
      <p class="bold space-x1 w25">
        Start State:
      </p>
      <div class="w100 flex">
        <div class="w50">
          {
          <div v-for="startId in start.value" class="space-x1 inline">
            <div v-if="startId != start.value[start.value.length-1]" @click="editPredicate(startId)"
                 class="unselected hover-pointer inline">
              {{ startId }},
            </div>
            <div v-else @click="editPredicate(startId)" class="unselected hover-pointer inline">
              {{ startId }}
            </div>
          </div>
          }
        </div>
        <div class="w50">
          {
          <div v-for="startId in start.value" class="space-x1 inline">
            <div v-if="startId != start.value[start.value.length-1] && this.predicates.has(startId)"
                 @click="editPredicate(startId)" class="unselected hover-pointer inline">
              {{ this.predicates.get(startId).result }},
            </div>
            <div v-else @click="editPredicate(startId) && this.predicates.has(startId)"
                 class="unselected hover-pointer inline">
              {{ this.predicates.get(startId).result }}
            </div>
          </div>
          }
        </div>
      </div>
    </div>
    <div class="w100 space-1x">
      <p class="bold space-x1 w25">
        End State:
      </p>
      <div class="w100 flex">
        <div class="w50">
          {
          <div v-for="startId in end" class="space-x1 inline">
            <div v-if="startId != end[end.length-1]" @click="editPredicate(startId)"
                 class="unselected hover-pointer inline">
              {{ startId }},
            </div>
            <div v-else @click="editPredicate(startId)" class="unselected hover-pointer inline">
              {{ startId }}
            </div>
          </div>
          }
        </div>
        <div class="w50">
          {
          <div v-for="startId in end" class="space-x1 inline">
            <div v-if="startId != end[end.length-1] && this.predicates.has(startId)"
                 @click="editPredicate(startId)" class="unselected hover-pointer inline">
              {{ this.predicates.get(startId).result }},
            </div>
            <div v-else @click="editPredicate(startId) && this.predicates.has(startId)"
                 class="unselected hover-pointer inline">
              {{ this.predicates.get(startId).result }}
            </div>
          </div>
          }
        </div>
      </div>
    </div>
    <div class="w100 space-1x">
      <div class="space-x1 flex">
        <div class="w25">
          <p class="bold space-x1">
            STRIPS Actions:
          </p>
        </div>
        <div class="w75">
          <button class="border space-x1 hover-background hover-pointer" @click="addAction()">
            Add STRIPS Action
          </button>
        </div>
      </div>
      <div v-for="action in actions.values()" class="w100 flex">
        <div class="w25 space-x1">
          <p @click="editAction(action.id)" class="unselected hover-pointer">
            {{ action.id }}:
          </p>
        </div>
        <div class="w75 space-x1">
          <p v-if="action.valid" class="valid">
            ACT: {{ action.result }}
          </p>
          <p v-else class="invalid">
            ACT: {{ action.result }}
          </p>
          <p v-if="action.valid" class="valid">
            PRE: {{ action.resultPre }}
          </p>
          <p v-else class="invalid">
            PRE: {{ action.resultPre }}
          </p>
          <p v-if="action.valid" class="valid">
            EFF: {{ action.resultEff }}
          </p>
          <p v-else class="invalid">
            EFF: {{ action.resultEff }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import type {Predicate} from "@/predicate";
import type {Action} from "@/action";
import {validatePredicate} from "@/predicate";

export default defineComponent({
  name: "PlanInput",
  components: {},
  props: {
    predicates: {
      type: Object as PropType<Map<String, Predicate>>,
      required: true,
    },
    actions: {
      type: Object as PropType<Map<String, Action>>,
      required: true,
    },
    start: {
      type: Object as PropType<Ref<string[]>>,
      required: true,
    },
    end: {
      type: Object as PropType<string[]>,
      required: true,
    }
  },
  emits: ["addPredicate", "addAction", "editPredicate", "editAction", "toggleStart", "toggleEnd"],
  methods: {
    validatePredicate,
    addPredicate() {
      this.$emit("addPredicate");
    },
    addAction() {
      this.$emit("addAction");
    },
    editPredicate(id: string) {
      this.$emit("editPredicate", id);
    },
    editAction(id: string) {
      this.$emit("editAction", id);
    },
    toggleStart(id: string) {
      this.$emit("toggleStart", id);
    },
    toggleEnd(id: string) {
      this.$emit("toggleEnd", id);
    },
  }
});
</script>

<style scoped lang="scss">

</style>