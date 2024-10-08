<template>
  <header class="w100 background-nav space-1x fixed">
    <nav class="w100 flex">
      <RouterLink :class="homeClass + ' bold font-1x5 text-shadow'" to="/home">Settings</RouterLink>
      <RouterLink :class="inputClass + ' bold font-1x5 text-shadow'" to="/input">Input</RouterLink>
      <RouterLink :class="outputClass + ' bold font-1x5 text-shadow'" to="/output">Output</RouterLink>
    </nav>
  </header>
  <div class="h100 w100 z-1 background-main fixed">
  </div>
  <div class="top-padding-main">
    <RouterView/>
  </div>
</template>

<script lang="ts">
import {RouterLink, RouterView, useRouter} from 'vue-router'
import {defineComponent, reactive, ref, nextTick} from "vue";
import HomeView from "@/views/HomeView.vue";
import InputView from "@/views/InputView.vue";
import OutputView from "@/views/OutputView.vue";
import {newOutput} from "@/output";
import {defaultColors} from "@/colors";
import type {Predicate} from "@/predicate";
import type {Action} from "@/action";
import type {Output} from "@/output";
import type {Colors} from "@/colors";

export default defineComponent({
  setup() {
    let inputClass = ref<String>(null);
    let outputClass = ref<String>(null);
    let homeClass = ref<String>(null);

    const unselect = function () {
      inputClass.value = "unselected";
      outputClass.value = "unselected";
      homeClass.value = "unselected";
    }

    let predicates = new Map<String, Predicate>;
    let actions = new Map<String, Action>;
    let start = [] as string[];
    let end = [] as string[];
    let output = newOutput() as Output;
    let txtInput = "" as string;
    let colors = defaultColors();
    let editKind = "" as string;

    let refStart = ref(start);
    let refEnd = ref(end);
    let refOutput = ref(output);
    let refTxtInput = ref(txtInput);
    let refColors = ref(colors);
    let plannerCreated = ref(false);
    let refEditKind = ref(editKind);

    const response = fetch("/colors.json");
    response.then(value => {
      value.json().then((c) => {
        refColors.value = c;
        const elemsBN = document.getElementsByClassName("background-nav");
        for (let i = 0; i < elemsBN.length; i = i + 1) {
          (elemsBN.item(i) as HTMLElement).style.background = refColors.value.backgroundNav;
        }
        const elemsB = document.getElementsByClassName("background-main");
        for (let i = 0; i < elemsB.length; i = i + 1) {
          (elemsB.item(i) as HTMLElement).style.background = refColors.value.background;
        }
        const elemsUN = document.getElementsByClassName("unselected");
        for (let i = 0; i < elemsUN.length; i = i + 1) {
          (elemsUN.item(i) as HTMLElement).style.color = refColors.value.unselected;
        }
        const elemsS = document.getElementsByClassName("selected");
        for (let i = 0; i < elemsS.length; i = i + 1) {
          (elemsS.item(i) as HTMLElement).style.color = refColors.value.selected;
        }

        const idBN = document.getElementById("navbar-background-color-input");
        (idBN as HTMLInputElement).value = refColors.value.backgroundNav;
        const idB = document.getElementById("background-color-input");
        (idB as HTMLInputElement).value = refColors.value.background;
        const idUS = document.getElementById("unselected-color-input");
        (idUS as HTMLInputElement).value = refColors.value.unselected;
        const idS = document.getElementById("selected-color-input");
        (idS as HTMLInputElement).value = refColors.value.selected;
      })
    });

    useRouter().addRoute({path: '/', component: HomeView, props: {}});
    useRouter().addRoute({path: '/home', component: HomeView, props: {
        colors: refColors,
      }
    })
    useRouter().addRoute({
      path: '/input',
      component: InputView,
      props: {
        predicates: reactive(predicates),
        actions: reactive(actions),
        start: refStart,
        end: refEnd,
        plannerCreated: plannerCreated,
        output: refOutput,
        txtInput: refTxtInput,
        editKind: refEditKind
      }
    })
    useRouter().addRoute({
      path: '/output',
      component: OutputView,
      props: {
        predicates: reactive(predicates),
        actions: reactive(actions),
        start: refStart,
        end: refEnd,
        plannerCreated: plannerCreated,
        output: refOutput,
        txtInput: refTxtInput,
        editKind: refEditKind
      }
    })

    useRouter().afterEach((guard) => {
      unselect();

      switch (guard.path) {
        case "/": {
          homeClass.value = "selected";
          break;
        }
        case "/home": {
          homeClass.value = "selected";
          break;
        }
        case "/input": {
          inputClass.value = "selected";
          break;
        }
        case "/output": {
          outputClass.value = "selected";
          break;
        }
      }

      nextTick(() => {
        const elemsUN = document.getElementsByClassName("unselected");
        for (let i = 0; i < elemsUN.length; i = i + 1) {
          (elemsUN.item(i) as HTMLElement).style.color = refColors.value.unselected;
        }
        const elemsS = document.getElementsByClassName("selected");
        for (let i = 0; i < elemsS.length; i = i + 1) {
          (elemsS.item(i) as HTMLElement).style.color = refColors.value.selected;
        }
      })
    });

    useRouter().replace("/home");

    return {
      inputClass,
      outputClass,
      homeClass,
      predicates,
      actions,
      start,
      end,
    };
  },
  name: "IndexRouter",
  components: {RouterLink, RouterView, HomeView},
});

</script>

<style lang="scss">
@import url('https://fonts.googleapis.com/css2?family=Source+Code+Pro:wght@400;700&display=swap');

* {
  margin: 0;
  padding: 0;
  font-size: 16px;
  border-style: none;
  background: none;
  box-sizing: border-box;
  flex-wrap: wrap;
  outline: none;
  text-decoration: none;
  color: #fff;
  font-family: 'Source Code Pro', monospace;
  font-weight: 400;
  -moz-appearance: textfield;
}

.top-padding-main {
  padding-top: calc(32px + 2rem);
}

.hover-pointer {
  &:hover {
    cursor: pointer;
  }
}

.border {
  border-style: solid;
  border-width: 1px;
  border-color: #000;
  border-radius: 3px;
}

.border-output {
  border-style: solid;
  border-width: 1px;
  border-color: #4D7A97;
  border-radius: 3px;
}

.border-action {
  border-style: solid;
  border-width: 1px;
  border-color: #c0bc29;
  border-radius: 3px;
}

.hover-background {
  &:hover {
    background-color: #4A6782;
  }
}

.underline {
  border-bottom: #000000 1px solid;
}

.underline-input {
  border-bottom: #5ca2c7 1px solid;
}

html, body, div#app {
  height: 100%;
  width: 100%;
}

.inline {
  display: inline;

  &.flex {
    display: inline-flex;
  }
}

.sticky {
  position: sticky;
  top: 0;
}

.fixed {
  position: fixed;
}

.text-shadow {
  text-shadow: #000 0 0 2px;
}

.font-1x5 {
  font-size: 24px;
}

.bold {
  font-weight: 700;
}

.flex {
  display: flex;
  justify-content: space-between;
  align-content: baseline;
}

.flex-center {
  display: flex;
  justify-content: center;
  align-content: center;
}

.space-1x {
  padding: 1rem;
}

.space-x25 {
  padding: 0.25rem;
}

.space-x1 {
  padding: 0.1rem;
}

.space-w5 {
  padding-left: 0.5rem;
  padding-right: 0.5rem;
}

.h100 {
  height: 100%;
}

.z-1 {
  z-index: -1;
}

.w-fit {
  width: fit-content;
}

.w100 {
  width: 100%;
}

.w80 {
  width: 80%;
}

.w75 {
  width: 75%;
}

.w66 {
  width: 66.66%;
}

.w60 {
  width: 60%;
}

.w50 {
  width: 50%;
}

.w40 {
  width: 40%;
}

.w33 {
  width: 33.33%;
}

.w25 {
  width: 25%;
}

.w20 {
  width: 20%;
}

.w10 {
  width: 10%;
}

.selected {
  text-transform: uppercase;
  color: #5ca2c7;
}

.unselected {
  color: #4A6782;
}

.background-nav {
  background: #253441;
}

.background-main {
  background: #162a37;
}

.valid {
  color: #048f31;
}

.invalid {
  color: #dd2e2e;
}

</style>
