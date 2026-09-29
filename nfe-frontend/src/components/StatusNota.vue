<script setup>
defineProps({ status: { type: String, required: true } })
</script>

<template>
  <span class="status" :class="status.toLowerCase()">{{ status }}</span>
</template>
