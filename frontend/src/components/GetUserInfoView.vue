<template>

    <v-data-table
        :headers="headers"
        :items="getUserInfo"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'GetUserInfoView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
            ],
            getUserInfo : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/getUserInfos'))

            temp.data._embedded.getUserInfos.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.getUserInfo = temp.data._embedded.getUserInfos;
        },
        methods: {
        }
    }
</script>

