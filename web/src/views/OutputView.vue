<template>
  <main>
    <div class="flex w100">
      <div class="w20 space-1x flex-center">
        <button v-if="editKind.value == 'txt-input'"
                class="valid border space-x1 hover-background hover-pointer" @click="loadTxt()">
          Reset Txt Input
        </button>
        <button v-else-if="allValid(actions,predicates)"
                class="valid border space-x1 hover-background hover-pointer" @click="createPlanner()">
          New Planner
        </button>
        <button v-else class="invalid border space-x1">
          New Planner
        </button>
      </div>
      <div class="w20 space-1x flex-center">
        <button
          v-if="allValid(actions,predicates) && plannerCreated.value && !output.value.complete && !output.value.undoable && !output.value.undo"
          class="valid border space-x1 hover-background hover-pointer" @click="nextPlanStep()">
          Next Step
        </button>
        <button v-else class="invalid border space-x1">
          Next Step
        </button>
      </div>
      <div class="w20 space-1x flex-center">
        <button
          v-if="allValid(actions,predicates) && plannerCreated.value && !output.value.complete && !output.value.undoable && output.value.undo"
          class="valid border space-x1 hover-background hover-pointer" @click="undoPlanStep()">
          Backtrack
        </button>
        <button v-else class="invalid border space-x1">
          Backtrack
        </button>
      </div>
      <div class="w20 space-1x flex-center">
        <button
          v-if="allValid(actions,predicates) && plannerCreated.value && output.value.previousStepExists"
          class="valid border space-x1 hover-background hover-pointer" @click="previousStep()">
          Previous Step
        </button>
        <button v-else class="invalid border space-x1">
          Previous Step
        </button>
      </div>
      <div class="w20 space-1x flex-center">
        <button
          v-if="allValid(actions,predicates) && plannerCreated.value && !output.value.complete && !output.value.undoable && !output.value.undo"
          class="valid border space-x1 hover-background hover-pointer" @click="runPlanner()">
          Complete Steps
        </button>
        <button v-else class="invalid border space-x1">
          Complete Steps
        </button>
      </div>
    </div>

    <div class="w100 space-1x">
      <p v-if="output.value.undoable && plannerCreated.value" class="bold space-x1">
        Output: (undoable)
      </p>
      <p v-else-if="output.value.complete && plannerCreated.value" class="bold space-x1">
        Output: (complete)
      </p>
      <p v-else-if="plannerCreated.value && output.value.undo" class="bold space-x1">
        Output: ({{ output.value.undoType }})
      </p>
      <p v-else-if="plannerCreated.value" class="bold space-x1">
        Output: (incomplete)
      </p>
      <p v-else class="bold space-x1">
        Output: (not initialized)
      </p>
      <div class="space-x25">
        <canvas :ref="(el) => setCanvas(el)" width="0" height="0"></canvas>
        <div class="border-output w100">
          <div v-if="output.value.actionsName.length>0" class="flex w100">
            <div class="flex w100 space-x1">
              <ActionOutput :output="output.value" :actionIndex="0" class="w100"></ActionOutput>
              <div class="w100 space-1x"></div>
            </div>
            <div v-for="index in (output.value.actionsName.length-2)" class="flex w50 space-x1">
              <ActionOutput :output="output.value" :actionIndex="index+1" class="w100"></ActionOutput>
              <div class="w100 space-1x"></div>
            </div>
            <div class="flex w100 space-x1">
              <ActionOutput :output="output.value" :actionIndex="1" class="w100"></ActionOutput>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="space-x1">
      <div class="w100 underline"></div>
    </div>

    <div class="space-x1">
      <div>
        <p class="bold space-x1">
          Partial Order
        </p>
      </div>
      <div class="border-output">
        {
        <em v-for="index in output.value.tLinksTo.length">
          {{ output.value.actionsName[output.value.tLinksFrom[index-1]] }} &lt {{ output.value.actionsName[output.value.tLinksTo[index-1]] }};
        </em>
        <em v-for="index in output.value.cLinksTo.length">
          {{ output.value.actionsName[output.value.cLinksFromAction[index-1]] }} &lt {{ output.value.actionsName[output.value.cLinksToAction[index-1]] }};
        </em>
        }
      </div>
    </div>

    <div class="space-x1">
      <div>
        <p class="bold space-x1">
          Description
        </p>
      </div>
      <div class="border-output">
        <p v-for="description in output.value.descriptions">
          - {{ description }}
        </p>
      </div>
    </div>

  </main>
</template>

<script lang="ts">
import {defineComponent, PropType, ref, Ref} from "vue";
import type {Predicate} from "@/predicate";
import type {Action} from "@/action";
import {allValid} from "@/action";
import type {Output} from "@/output";
import {newOutput} from "@/output";
import ActionOutput from "@/components/ActionOutput.vue";

export default defineComponent({
  setup() {
    return {
      canvas: ref<HTMLCanvasElement | null>(null),
    }
  },
  name: "HomeView",
  components: {ActionOutput},
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
  methods: {
    allValid,
    async runPlanner() {
      const response = await fetch("/runPlanner", {method: "POST"});
      response.json().then((out: Output) => this.output.value = out);
      setTimeout(this.drawTLinks, 50);
      setTimeout(this.drawCLinks, 50);
    },
    async nextPlanStep() {
      const response = await fetch("/nextPlanStep", {method: "POST"});
      response.json().then((out: Output) => this.output.value = out);
      setTimeout(this.drawTLinks, 50);
      setTimeout(this.drawCLinks, 50);
    },
    async undoPlanStep() {
      const response = await fetch("/undoPlanStep", {method: "POST"});
      response.json().then((out: Output) => this.output.value = out);
      setTimeout(this.drawTLinks, 50);
      setTimeout(this.drawCLinks, 50);
    },
    async previousStep() {
      const response = await fetch("/previousPlanStep", {method: "POST"});
      response.json().then((out: Output) => this.output.value = out);
      setTimeout(this.drawTLinks, 50);
      setTimeout(this.drawCLinks, 50);
    },
    async createPlanner() {
      const response = await fetch("/createPlanner", {
        method: "POST",
        body: JSON.stringify({
          predicates: Array.from(this.predicates.entries()),
          actions: Array.from(this.actions.entries()),
          start: this.start.value,
          end: this.end.value
        })
      });
      response.json().then((out: Output) => this.output.value = out);
      setTimeout(this.drawTLinks, 50);
      setTimeout(this.drawCLinks, 50);
      this.plannerCreated.value = true;
    },
    setCanvas(el: unknown) {
      this.canvas = el as HTMLCanvasElement;
    },
    transformIndex(index: number) : number {
      if (index < 0 || index >= this.output.value.actionsName.length) {
        return -1;
      }
      if (index === 0) {
        return 0;
      }
      if (index === 1) {
        return this.output.value.actionsName.length-1;
      }
      return index-1;
    },
    drawTLinks() {
      if (this.canvas) {
        const div: HTMLDivElement = this.canvas!.parentElement!.children[1] as HTMLDivElement;
        const context = this.canvas.getContext("2d");
        context.clearRect(0, 0, this.canvas.width, this.canvas.height);
        context.beginPath();
        for (let i = 0; i < this.output.value.tLinksFrom.length; i = i + 1) {
          let from = this.output.value.tLinksFrom[i];

          const nameDivFrom: HTMLDivElement = div.children[0].children[this.transformIndex(from)].children[0].children[0].children[0].children[this.output.value.actionsPreconditions[from].length] as HTMLDivElement;
          const leftFrom = nameDivFrom.offsetLeft + nameDivFrom.clientWidth / 2 - this.canvas.offsetLeft;
          const topFrom = nameDivFrom.offsetTop + nameDivFrom.clientHeight - this.canvas.offsetTop;

          let to = this.output.value.tLinksTo[i];

          const nameDivTo: HTMLDivElement = div.children[0].children[this.transformIndex(to)].children[0].children[0].children[0].children[this.output.value.actionsPreconditions[to].length] as HTMLDivElement;
          const leftTo = nameDivTo.offsetLeft + nameDivTo.clientWidth / 2 - this.canvas.offsetLeft;
          const topTo = nameDivTo.offsetTop - this.canvas.offsetTop;
          context.moveTo(leftFrom, topFrom);
          context.lineTo(leftTo, topTo);
          context.strokeStyle = "#0000ff";
          context.lineWidth = 3;
          const x = (leftFrom - leftTo);
          const y = (topFrom - topTo);
          const d = Math.sqrt(x * x + y * y);
          const xArrow = x / d * 10;
          const yArrow = y / d * 10;
          const x1 = xArrow * Math.cos(Math.PI / 6) - yArrow * Math.sin(Math.PI / 6)
          const y1 = xArrow * Math.sin(Math.PI / 6) + yArrow * Math.cos(Math.PI / 6)
          context.lineTo(x1 + leftTo, y1 + topTo);
          const x2 = xArrow * Math.cos(-Math.PI / 6) - yArrow * Math.sin(-Math.PI / 6)
          const y2 = xArrow * Math.sin(-Math.PI / 6) + yArrow * Math.cos(-Math.PI / 6)
          context.lineTo(x2 + leftTo, y2 + topTo);
          context.moveTo(leftTo, topTo);
          context.lineTo(x2 + leftTo, y2 + topTo);
          context.lineTo(x1 + leftTo, y1 + topTo);
        }
        context.stroke();
        context.closePath();
      }
    },
    drawCLinks() {
      if (this.canvas) {
        const div: HTMLDivElement = this.canvas!.parentElement!.children[1] as HTMLDivElement;
        const context = this.canvas.getContext("2d");
        context.beginPath();
        for (let i = 0; i < this.output.value.cLinksFrom.length; i = i + 1) {
          let from = this.output.value.cLinksFromAction[i];

          let indexPredFrom = this.output.value.cLinksFrom[i];
          let yShiftFrom = 0;
          if (!this.output.value.cLinksFromPre[i]) {
            indexPredFrom = indexPredFrom + this.output.value.actionsPreconditions[from].length + 1;
            yShiftFrom = 1;
          }
          const nameDivFrom: HTMLDivElement = div.children[0].children[this.transformIndex(from)].children[0].children[0].children[0].children[indexPredFrom] as HTMLDivElement;
          const leftFrom = nameDivFrom.offsetLeft + nameDivFrom.clientWidth / 2 - this.canvas.offsetLeft;
          const topFrom = nameDivFrom.offsetTop + nameDivFrom.clientHeight * (yShiftFrom) - this.canvas.offsetTop;

          let to = this.output.value.cLinksToAction[i];

          let indexPredTo = this.output.value.cLinksTo[i];
          let yShiftTo = 0;
          if (!this.output.value.cLinksToPre[i]) {
            indexPredTo = indexPredTo + this.output.value.actionsPreconditions[to].length + 1;
            yShiftTo = 1;
          }
          const nameDivTo: HTMLDivElement = div.children[0].children[this.transformIndex(to)].children[0].children[0].children[0].children[indexPredTo] as HTMLDivElement;
          const leftTo = nameDivTo.offsetLeft + nameDivTo.clientWidth / 2 - this.canvas.offsetLeft;
          const topTo = nameDivTo.offsetTop + nameDivTo.clientHeight * (yShiftTo) - this.canvas.offsetTop;
          context.moveTo(leftFrom, topFrom);
          context.lineTo(leftTo, topTo);
          const x = (leftFrom - leftTo);
          const y = (topFrom - topTo);
          const d = Math.sqrt(x * x + y * y);
          const xArrow = x / d * 10;
          const yArrow = y / d * 10;
          const x1 = xArrow * Math.cos(Math.PI / 6) - yArrow * Math.sin(Math.PI / 6)
          const y1 = xArrow * Math.sin(Math.PI / 6) + yArrow * Math.cos(Math.PI / 6)
          context.lineTo(x1 + leftTo, y1 + topTo);
          const x2 = xArrow * Math.cos(-Math.PI / 6) - yArrow * Math.sin(-Math.PI / 6)
          const y2 = xArrow * Math.sin(-Math.PI / 6) + yArrow * Math.cos(-Math.PI / 6)
          context.lineTo(x2 + leftTo, y2 + topTo);
          context.moveTo(leftTo, topTo);
          context.lineTo(x2 + leftTo, y2 + topTo);
          context.lineTo(x1 + leftTo, y1 + topTo);
          context.strokeStyle = "#00ff00";
          context.lineWidth = 3;
        }
        context.stroke();
        context.closePath();
      }
    },
    async loadTxt() {
      const output = this.output;
      const plannerCreated = this.plannerCreated;
      const txtInput = this.txtInput;
      const response = await fetch("/loadtxt", {method: "POST", body: txtInput.value});
      response.json().then((out: Output) => output.value = out);
      plannerCreated.value = true;
    },
  },
  mounted() {
    if (this.canvas) {
      const div: HTMLDivElement = this.canvas!.parentElement!.children[1] as HTMLDivElement;
      this.canvas.style.zIndex = "-1";
      this.canvas.style.backgroundColor = "#000";
      this.canvas.style.position = "absolute";
      const observer = new ResizeObserver(() => {
        if (this.canvas) {
          this.canvas!.width = div.clientWidth;
          this.canvas!.height = div.clientHeight;
        }
      });
      observer.observe(div);
    }
  }
});
</script>
