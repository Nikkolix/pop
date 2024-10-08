<template>
  <div>
    <form action="" class="space-1x w100">
      <div class="w100 space-x1">
        <p class="bold">
          Predicate Input ({{ id }}) <em v-if="predicate().valid" class="bold valid">
          {{ predicate().validStatus }} </em> <em v-else class="bold invalid"> {{
            predicate().validStatus
          }} </em>
        </p>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Predicate: </label>
        </div>
        <div class="w75">
          <input type="text" :class="predicate().resultClass+' underline-input w100'" placeholder="name(arg)"
                 :value="predicate().result"
                 @input="(event) => resultInput(event,actions,predicates,predicate(),start,end)"
                 @change="calcResult(actions,predicates,predicate(),start,end)">
        </div>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Name: </label>
        </div>
        <div class="w75">
          <input :class="predicate().nameClass + ' underline-input w100'" placeholder="name" type="text"
                 v-model="predicate().name" @input="calcResult(actions,predicates,predicate(),start,end)">
        </div>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Negation: </label>
        </div>
        <div class="w75">
          <input type="checkbox" v-model="predicate().negation"
                 @change="calcResult(actions,predicates,predicate(),start,end)">
        </div>
      </div>
      <div class="w100 flex space-x1">
        <div class="w25">
          <label class="w100">Number of Arguments: </label>
        </div>
        <div class="w75">
          <input :class="predicate().numArgsClass + ' underline-input w100'" type="text"
                 v-model="predicate().numArgs" placeholder="0"
                 @input="changeNumberOfArgs(); calcResult(actions,predicates,predicate(),start,end);">
        </div>
      </div>
      <div class="w100 flex space-x1" v-for="i in max(Number(predicate().numArgs),0)">
        <div class="w25">
          <label class="w100">Argument {{ i }}: </label>
        </div>
        <div class="w75">
          <input :class="predicate().argsClass[i-1] + ' underline-input w100'" type="text"
                 placeholder="argument" v-model="predicate().args[i-1]"
                 @input="changeArgument(i-1); calcResult(actions,predicates,predicate(),start,end)">
          <div>
            <input type="radio" :name="'type'+i" :id="'literal'+i" value="literal"
                   v-model="predicate().literal[i-1]"
                   checked="checked"
                   @input="changeKind(i-1,'literal'); calcResult(actions,predicates,predicate(),start,end)">
            <label :id="'literal'+i">literal</label>
          </div>
          <div>
            <input type="radio" :name="'type'+i" :id="'variable'+i" value="variable"
                   v-model="predicate().variable[i-1]"
                   @input="changeKind(i-1,'variable'); calcResult(actions,predicates,predicate(),start,end)">
            <label :for="'variable'+i">variable</label>
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
import {calcResult, propagateChanges, resultInput, validatePredicate} from "@/predicate";
import {max} from "@/utility";

export default defineComponent({
  name: "PredicateInput",
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
    resultInput,
    calcResult,
    predicate() {
      return this.predicates.get(this.id);
    },
    changeNumberOfArgs() {
      while (this.predicate().args.length < Number(this.predicate().numArgs)) {
        this.predicate().args.push("Arg");
      }

      for (let i = 0; i < this.predicate().args.length; i = i + 1) {
        if (this.predicate().args[i] == "") {
          this.predicate().args[i] = "Arg";
        }
      }
    },
    changeKind(i: number, to: String) {
      if (to == "literal") {
        this.predicate().kind[i] = "literal";
        if (this.predicate().args[i].charAt(0) >= 'a' && this.predicate().args[i].charAt(0) <= 'z') {
          this.predicate().args[i] = this.predicate().args[i].charAt(0).toUpperCase() + this.predicate().args[i].slice(1, this.predicate().args[i].length)
        }
      } else if (to == "variable") {
        this.predicate().kind[i] = "variable";
        if (this.predicate().args[i].charAt(0) >= 'A' && this.predicate().args[i].charAt(0) <= 'Z') {
          this.predicate().args[i] = this.predicate().args[i].charAt(0).toLowerCase() + this.predicate().args[i].slice(1, this.predicate().args[i].length)
        }
      }
    },
    changeArgument(i: number) {
      if (this.predicate().args[i].charAt(0) >= 'A' && this.predicate().args[i].charAt(0) <= 'Z') {
        this.predicate().kind[i] = "literal";
        this.predicate().literal[i] = "literal";
        this.predicate().variable[i] = "";
        return;
      }
      if (this.predicate().args[i].charAt(0) >= 'a' && this.predicate().args[i].charAt(0) <= 'z') {
        this.predicate().kind[i] = "variable";
        this.predicate().literal[i] = "";
        this.predicate().variable[i] = "variable";
        return;
      }
    },
  },
  watch: {
    id: function (newValue: string, oldValue: string) {
      calcResult(this.actions, this.predicates, this.predicate(),this.start,this.end);
    }
  }
});
</script>

<style scoped lang="scss">

</style>