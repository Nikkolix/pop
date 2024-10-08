<template>
  <main class="w100">
    <div>
      <div class="w100 space-1x flex">
        <div class="w25 flex-center">
          <button v-if="allValid(this.actions,this.predicates)"
                  class="border valid hover-background hover-pointer space-x1" @click="event => save(event)">
            Save
          </button>
          <button v-else class="border space-x1 invalid">
            Save
          </button>
        </div>
        <div class="w25 flex-center">
          <button class="border hover-background hover-pointer space-x1" @click="event => load(event)">
            Load-JSON
          </button>
        </div>
        <div class="w25 flex-center">
          <button v-if="editKind.value != 'txt-input'"  class="border hover-background hover-pointer space-x1" @click="event => loadTxt(event)">
            Load-TXT
          </button>
          <button v-else class="border hover-background hover-pointer space-x1" @click="event => unloadTxt(event)">
            Unload-TXT
          </button>
        </div>
        <div class="w25 flex-center">
          <button class="border hover-background hover-pointer space-x1" @click="clear()">
            Clear
          </button>
        </div>
      </div>
      <div class="w100 underline"></div>
      <PlanInput
        v-if="editKind.value != 'txt-input'"
        :end="end.value"
        :start="start"
        :actions="actions"
        :predicates="predicates"
        @add-action="addAction()"
        @add-predicate="addPredicate()"
        @editAction="(id) => editAction(id)"
        @editPredicate="(id) => editPredicate(id)"
        @toggleStart="(id) => toggleStart(id)"
        @toggleEnd="(id) => toggleEnd(id)"></PlanInput>
      <div class="w100 underline"></div>
      <PredicateInput v-if="editKind.value == 'predicate'" :id="editId" :predicates="predicates" :end="end.value"
                      :start="start.value"
                      :actions="actions"></PredicateInput>
      <STRIPSInput v-else-if="editKind.value == 'action'" :id="editId" :predicates="predicates" :end="end.value"
                   :start="start.value"
                   :actions="actions"></STRIPSInput>
      <div v-else-if="editKind.value == 'txt-input'" class="space-1x">
        <p v-for="line in txtInput.value.split('\r')"> {{ line }} </p>
      </div>
    </div>
  </main>
</template>

<script lang="ts">
import {defineComponent, PropType, Ref} from "vue";
import PredicateInput from "../components/PredicateInput.vue";
import STRIPSInput from "../components/STRIPSInput.vue";
import PlanInput from "@/components/PlanInput.vue";
import type {Predicate} from "@/predicate";
import type {Action} from "@/action";
import {allValid} from "@/action";

export default defineComponent({
  name: "HomeView",
  components: {PredicateInput, STRIPSInput, PlanInput},
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
      type: Object as PropType<Ref<string[]>>,
      required: true,
    },
    plannerCreated: {
      type: Object as PropType<Ref<boolean>>,
      required: true,
    },
    output: {
      type: Object as PropType<Ref<Output>>,
      required: true,
    },
    txtInput: {
      type: Object as PropType<Ref<string>>,
      required: true,
    },
    editKind: {
      type: Object as PropType<Ref<string>>,
      required: true,
    }
  },
  data() {
    return {
      editId: "",
      predId: this.predicates.size + 1,
      actId: this.actions.size + 1,
    }
  },
  methods: {
    allValid,
    addPredicate() {
      let id = "P" + this.predId.toString();
      let predicate: Predicate = {
        result: "name(Arg)",
        resultClass: "valid",
        id: id,
        name: "name",
        nameClass: "valid",
        negation: false,
        numArgs: "1",
        numArgsClass: "valid",
        args: ["Arg"],
        argsClass: ["valid"],
        kind: ["literal"],
        literal: ["literal"],
        variable: [""],
        valid: true,
        validStatus: "OK",
      }
      this.predicates.set(id, predicate);
      this.predId = this.predId + 1;
      this.editId = id;
      this.editKind.value = "predicate";
    },
    addAction() {
      let id = "A" + this.actId.toString();
      let action: Action = {
        result: "name()",
        resultPre: "{}",
        resultEff: "{}",
        id: id,
        name: "name",
        nameClass: "valid",
        priority: "0",
        priorityClass: "valid",
        numPre: "0",
        numPreClass: "valid",
        numEff: "0",
        numEffClass: "valid",
        pre: [],
        preClass: [],
        eff: [],
        effClass: [],
        valid: true,
        validStatus: "",
        numNeq: "0",
        numNeqArgs: [],
        neqArgs: [],
        numNeqClass: "valid",
        numNeqArgsClass: [],
        neqArgsClass: [],
      }
      this.actions.set(id, action);
      this.actId = this.actId + 1;
      this.editId = id;
      this.editKind.value = "action";
    },
    editPredicate(id: string) {
      this.editId = id;
      this.editKind.value = "predicate";
    },
    editAction(id: string) {
      this.editId = id;
      this.editKind.value = "action";
    },
    toggleStart(id: string) {
      if (this.start.value.includes(id)) {
        this.start.value = this.start.value.filter((startId) => startId != id);
        return;
      }
      this.start.value.push(id);
    },
    toggleEnd(id: string) {
      if (this.end.value.includes(id)) {
        this.end.value = this.end.value.filter((endId) => endId != id);
        return;
      }
      this.end.value.push(id);
    },
    save(event: Event) {
      const input = event.target as HTMLInputElement;
      const a = document.createElement("a") as HTMLAnchorElement;
      a.type = "application/json";
      a.download = "save.json";
      a.style.display = "none";
      a.style.visibility = "hidden";
      const json = JSON.stringify({
        predicates: Array.from(this.predicates.entries()),
        actions: Array.from(this.actions.entries()),
        start: this.start.value,
        end: this.end.value
      }, null, 2);
      const data = new Blob([json]);
      a.href = URL.createObjectURL(data);
      input.after(a);
      a.click();
      a.remove();
    },
    load(event: Event) {
      const input = event.target as HTMLInputElement;
      const fileInput = document.createElement("input") as HTMLInputElement;
      fileInput.type = "file";
      fileInput.accept = "application/json";
      fileInput.name = "load";
      fileInput.style.display = "none";
      fileInput.style.visibility = "hidden";
      input.after(fileInput);
      fileInput.click();
      const setData = this.setData;
      fileInput.onchange = (() => {
        const file = fileInput.files.item(0);
        const reader = new FileReader();
        reader.onload = (function () {
          return function (e) {
            const data = JSON.parse(e.target.result);
            setData(data);
          };
        })(file);
        reader.readAsText(file);
        fileInput.remove();
      })
    },
    loadTxt(event: Event) {
      const input = event.target as HTMLInputElement;
      const fileInput = document.createElement("input") as HTMLInputElement;
      fileInput.type = "file";
      fileInput.accept = "text/plain";
      fileInput.name = "load";
      fileInput.style.display = "none";
      fileInput.style.visibility = "hidden";
      input.after(fileInput);
      fileInput.click();
      const output = this.output;
      const plannerCreated = this.plannerCreated;
      const txtInput = this.txtInput;
      const setKind = this.setKind;
      fileInput.onchange = (() => {
        const file = fileInput.files.item(0);
        const reader = new FileReader();
        reader.onload = (function () {
          return async function (e) {
            const response = await fetch("/loadtxt", {method: "POST", body: e.target.result});
            response.json().then((out: Output) => output.value = out);
            plannerCreated.value = true;
            txtInput.value = e.target.result;
            setKind("txt-input");
          };
        })(file);
        reader.readAsText(file);
        fileInput.remove();
      })
    },
    unloadTxt(event: Event) {
      this.setKind("");
      this.txtInput.value = "";
    },
    clear() {
      this.editKind.value = "";
      this.editId = "";
      this.predId = 1;
      this.actId = 1;
      this.start.value = [] as string[];
      this.end.value = [] as string[];
      this.predicates.forEach((predicate, id) => this.predicates.delete(id));
      this.actions.forEach((action, id) => this.actions.delete(id));
    },
    setData(data: unknown) {
      this.editKind.value = "";
      this.editId = "";
      this.predId = data.predicates.length + 1;
      this.actId = data.actions.length + 1;
      this.start.value = [] as string[];
      this.end.value = [] as string[];
      this.predicates.forEach((predicate, id) => this.predicates.delete(id));
      this.actions.forEach((action, id) => this.actions.delete(id));
      data.predicates.forEach((predicate) => {
        this.predicates.set(predicate[0], predicate[1]);
      });
      data.actions.forEach((action) => {
        let setAction: Action = {
          result: "name()",
          resultPre: "{}",
          resultEff: "{}",
          id: "",
          name: "name",
          nameClass: "valid",
          priority: "0",
          priorityClass: "valid",
          numPre: "0",
          numPreClass: "valid",
          numEff: "0",
          numEffClass: "valid",
          pre: [],
          preClass: [],
          eff: [],
          effClass: [],
          valid: true,
          validStatus: "",
          numNeq: "0",
          numNeqArgs: [],
          neqArgs: [],
          numNeqClass: "valid",
          numNeqArgsClass: [],
          neqArgsClass: [],
        };
        Object.assign(setAction,action[1]);
        this.actions.set(action[0], setAction);
      });
      data.start.forEach((startId, index) => {
        this.start.value[index] = startId;
      });
      data.end.forEach((endId, index) => {
        this.end.value[index] = endId;
      });
    },
    setKind(kind: string) {
      this.editKind.value = kind;
    }
  }
});
</script>
