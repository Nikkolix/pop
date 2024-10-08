<template>
  <div>
    <div class="w100 space-x1">
      <div class="flex w100 border-action">
        <div class="flex-center" v-for="precondition in output.actionsPreconditions[actionIndex]"
             :style="'width: ' + (100/output.actionsPreconditions[actionIndex].length).toString() + '%;'">
          {{ precondition }}
        </div>
        <div class="w100 flex-center">
          <p>
            {{ output.actionsName[actionIndex] }}
          </p>
        </div>
        <div class="flex-center" v-for="effect in output.actionsEffects[actionIndex]"
             :style="'width: ' + (100/output.actionsEffects[actionIndex].length).toString() + '%;'">
          {{ effect }}
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue";
import type {Output} from "@/output";

export default defineComponent({
  name: "ActionOutput",
  components: {},
  props: {
    output: {
      type: Object as PropType<Output>,
      required: true,
    },
    actionIndex: {
      type: Number,
      required: true,
    },

  }
});

</script>

<style scoped lang="scss">

</style>