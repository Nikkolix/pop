<template>
  <div>
    <form action="" class="space-1x w100">
      <div class="w100 space-x1">
        <p class="bold">
          STRIPS Input ({{ id }}) <em v-if="action().valid" class="bold valid"> {{
            action().validStatus
          }} </em> <em v-else class="bold invalid"> {{ action().validStatus }} </em>
        </p>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Action: </label>
        </div>
        <div class="w75">
          <p class="underline w100">
            {{ action().result }}
          </p>
        </div>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Preconditions: </label>
        </div>
        <div class="w75">
          <p class="underline w100">
            {{ action().resultPre }}
          </p>
        </div>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Effects: </label>
        </div>
        <div class="w75">
          <p class="underline w100">
            {{ action().resultEff }}
          </p>
        </div>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Name: </label>
        </div>
        <div class="w75">
          <input :class="action().nameClass + ' underline-input w100'" placeholder="name" type="text"
                 v-model="action().name" @input="calcActionResult(actions,predicates,action(),start,end)">
        </div>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Priority: </label>
        </div>
        <div class="w75">
          <input :class="action().priorityClass + ' underline-input w100'" placeholder="0" type="text"
                 v-model="action().priority" @input="calcActionResult(actions,predicates,action(),start,end)">
        </div>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Number of Preconditions: </label>
        </div>
        <div class="w75">
          <input :class="action().numPreClass + ' underline-input w100'" type="text" v-model="action().numPre"
                 @input="changeNumberOfPreconditions(); calcActionResult(actions,predicates,action(),start,end);">
        </div>
      </div>
      <div class="w100 space-x25">
        <div class="w100 flex space-x1" v-for="i in max(Number(action().numPre),0)">
          <div class="w25">
            <label class="w100">Precondition {{ i }}: </label>
          </div>
          <div class="w25">
            <input :class="action().preClass[i-1] + ' underline-input w100'" type="text" placeholder="P"
                   v-model="action().pre[i-1]" @input="calcActionResult(actions,predicates,action(),start,end)">
          </div>
          <div class="w50">
            <p v-if="predicates.has(action().pre[i-1])" class="w100 underline valid space-w5">
              {{ predicates.get(action().pre[i - 1]).result }}
            </p>
            <p v-else class="w100 underline invalid space-w5">
              not found
            </p>
          </div>
        </div>
      </div>

      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Number of Unequals: </label>
        </div>
        <div class="w75">
          <input :class="action().numNeqClass + ' underline-input w100'" type="text" v-model="action().numNeq"
                 @input="changeNumberOfUnequals(); calcActionResult(actions,predicates,action(),start,end);">
        </div>
      </div>
      <div class="w100 space-x25">
        <div class="w100 flex space-x1" v-for="i in max(Number(action().numNeq),0)">
          <div class="w25">
            <label class="w100">Number of Args {{ i }}: </label>
          </div>
          <div class="w75">
            <input :class="action().numNeqArgsClass[i-1] + ' underline-input w100'" type="text" placeholder="0"
                   v-model="action().numNeqArgs[i-1]" @input="changeNumberOfUnequalArgs(i-1); calcActionResult(actions,predicates,action(),start,end)">
          </div>

          <div class="w100 space-x25">
            <div class="w100 flex space-x1" v-for="j in max(Number(action().numNeqArgs[i-1]),0)">
              <div class="w25">
                <label class="w100">Args {{ j }}: </label>
              </div>
              <div class="w75">
                <input :class="action().neqArgsClass[i-1][j-1] + ' underline-input w100'" type="text" placeholder="V"
                       v-model="action().neqArgs[i-1][j-1]" @input="calcActionResult(actions,predicates,action(),start,end)">
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Number of Effects: </label>
        </div>
        <div class="w75">
          <input :class="action().numEffClass + ' underline-input w100'" type="text" v-model="action().numEff"
                 @input="changeNumberOfEffects(); calcActionResult(actions,predicates,action(),start,end);">
        </div>
      </div>
      <div class="w100 space-x25">
        <div class="w100 flex space-x1" v-for="i in max(Number(action().numEff),0)">
          <div class="w25">
            <label class="w100">Effect {{ i }}: </label>
          </div>
          <div class="w25">
            <input :class="action().effClass[i-1] + ' underline-input w100'" type="text" placeholder="P"
                   v-model="action().eff[i-1]" @input="calcActionResult(actions,predicates,action(),start,end)">
          </div>
          <div class="w50">
            <p v-if="predicates.has(action().eff[i-1]) && predicates.get(action().eff[i-1]).valid"
               class="w100 underline valid space-w5">
              {{ predicates.get(action().eff[i - 1]).result }}
            </p>
            <p v-else-if="predicates.has(action().eff[i-1])" class="w100 underline invalid space-w5">
              {{ predicates.get(action().eff[i - 1]).result }}
            </p>
            <p v-else class="w100 underline invalid space-w5">
              not found
            </p>
          </div>
        </div>
      </div>

    </form>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import type {Predicate} from "@/predicate";
import type {Action} from "@/action";
import {calcActionResult} from "@/action";
import {max} from "@/utility";

export default defineComponent({
  name: "STRIPSInput",
  components: {},
  props: {
    id: {
      type: String,
      required: true,
    },
    predicates: {
      type: Object as PropType<Map<String, Predicate>>,
      required: true,
    },
    actions: {
      type: Object as PropType<Map<String, Action>>,
      required: true,
    },
    start: {
      type: Object as PropType<string[]>,
      required: true,
    },
    end: {
      type: Object as PropType<string[]>,
      required: true,
    }
  },
  methods: {
    max,
    calcActionResult,
    action() {
      return this.actions.get(this.id);
    },
    changeNumberOfPreconditions() {
      while (this.action().pre.length < Number(this.action().numPre)) {
        this.action().pre.push("P");
      }

      for (let i = 0; i < this.action().pre.length; i = i + 1) {
        if (this.action().pre[i] == "") {
          this.action().pre[i] = "P";
        }
      }
    },
    changeNumberOfEffects() {
      while (this.action().eff.length < Number(this.action().numEff)) {
        this.action().eff.push("P");
      }

      for (let i = 0; i < this.action().eff.length; i = i + 1) {
        if (this.action().eff[i] == "") {
          this.action().eff[i] = "P";
        }
      }
    },
    changeNumberOfUnequals() {
      while (this.action().neqArgs.length < Number(this.action().numNeq)) {
        this.action().neqArgs.push(["",""]);
      }
      this.action().neqArgs = this.action().neqArgs.slice(0,Number(this.action().numNeq))
      while (this.action().numNeqArgs.length < Number(this.action().numNeq)) {
        this.action().numNeqArgs.push("2");
      }
      this.action().numNeqArgs = this.action().numNeqArgs.slice(0,Number(this.action().numNeq))
      while (this.action().numNeqArgsClass.length < Number(this.action().numNeq)) {
        this.action().numNeqArgsClass.push("valid");
      }
      this.action().numNeqArgsClass = this.action().numNeqArgsClass.slice(0,Number(this.action().numNeq))
      while (this.action().neqArgsClass.length < Number(this.action().numNeq)) {
        this.action().neqArgsClass.push(["invalid","invalid"]);
      }
      this.action().neqArgsClass = this.action().neqArgsClass.slice(0,Number(this.action().numNeq))
    },
    changeNumberOfUnequalArgs(index: number) {
      while (this.action().neqArgs[index].length < Number(this.action().numNeqArgs[index])) {
        this.action().neqArgs[index].push("");
      }
      this.action().neqArgs[index] = this.action().neqArgs[index].slice(0,Number(this.action().numNeqArgs[index]))
      while (this.action().neqArgsClass[index].length < Number(this.action().numNeqArgs[index])) {
        this.action().neqArgsClass[index].push("invalid");
      }
      this.action().neqArgsClass[index] = this.action().neqArgsClass[index].slice(0,Number(this.action().neqArgsClass[index]))
    },
  },
  watch: {
    id: function () {
      this.calcActionResult(this.actions, this.predicates, this.action());
    }
  }
});
</script>

<style scoped lang="scss">

</style>