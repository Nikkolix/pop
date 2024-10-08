<template>
  <main>

    <div class="flex space-1x w50">
      <label for="navbar-background-color-input"> Navbar Background Color</label>
      <input type="color"
             name="navbar-background-color"
             id="navbar-background-color-input"
             v-on:input="(e) => navbarBackgroundColorInput(e)"
             v-on:change="(e) => change(e)"/>
    </div>

    <div class="flex space-1x w50">
      <label for="background-color-input"> Background Color</label>
      <input type="color"
             name="background-color"
           id="background-color-input"
           v-on:input="(e) => backgroundColorInput(e)"
           v-on:change="(e) => change(e)"/>
    </div>

    <div class="flex space-1x w50">
      <label for="unselected-color"> Unselected Color</label>
      <input type="color"
             name="unselected-color"
             id="unselected-color-input"
             v-on:input="(e) => unselectedColorInput(e)"
             v-on:change="(e) => change(e)"/>
    </div>

    <div class="flex space-1x w50">
      <label for="selected-color"> Selected Color</label>
      <input type="color"
             name="selected-color"
             id="selected-color-input"
             v-on:input="(e) => selectedColorInput(e)"
             v-on:change="(e) => change(e)"/>
    </div>
  </main>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import type {Colors} from "@/colors";

export default defineComponent({
  name: "HomeView",
  props: {
    colors: {
      type: Object as PropType<Ref<Colors>>,
      required: true,
    },
  },
  components: {},
  methods: {
    change(e) {
      fetch("/colors.json", {method: "POST", body: JSON.stringify(this.colors.value)});
    },
    navbarBackgroundColorInput(e) {
      const colorElem = e.target as HTMLInputElement;
      this.colors.value.backgroundNav = colorElem.value;
      const elems = document.getElementsByClassName("background-nav");
      for (let i = 0; i < elems.length; i = i + 1) {
        (elems.item(i) as HTMLElement).style.background = this.colors.value.backgroundNav;
      }
    },
    backgroundColorInput(e) {
      const colorElem = e.target as HTMLInputElement;
      this.colors.value.background = colorElem.value;
      const elems = document.getElementsByClassName("background-main");
      for (let i = 0; i < elems.length; i = i + 1) {
        (elems.item(i) as HTMLElement).style.background = this.colors.value.background;
      }
    },
    unselectedColorInput(e) {
      const colorElem = e.target as HTMLInputElement;
      this.colors.value.unselected = colorElem.value;
      const elems = document.getElementsByClassName("unselected");
      for (let i = 0; i < elems.length; i = i + 1) {
        (elems.item(i) as HTMLElement).style.color = this.colors.value.unselected;
      }
    },
    selectedColorInput(e) {
      const colorElem = e.target as HTMLInputElement;
      this.colors.value.selected = colorElem.value;
      const elems = document.getElementsByClassName("selected");
      for (let i = 0; i < elems.length; i = i + 1) {
        (elems.item(i) as HTMLElement).style.color = this.colors.value.selected;
      }
    }
  },
  mounted() {
    const idBN = document.getElementById("navbar-background-color-input");
    (idBN as HTMLInputElement).value = this.colors.value.backgroundNav;
    const idB = document.getElementById("background-color-input");
    (idB as HTMLInputElement).value = this.colors.value.background;
    const idUS = document.getElementById("unselected-color-input");
    (idUS as HTMLInputElement).value = this.colors.value.unselected;
    const idS = document.getElementById("selected-color-input");
    (idS as HTMLInputElement).value = this.colors.value.selected;
  }
});
</script>
