<template>

    <v-data-table
        :headers="headers"
        :items="admin"
        :items-per-page="5"
        class="elevation-1"
    ></v-data-table>

</template>

<script>
    const axios = require('axios').default;

    export default {
        name: 'AdminView',
        props: {
            value: Object,
            editMode: Boolean,
            isNew: Boolean
        },
        data: () => ({
            headers: [
                { text: "id", value: "id" },
            ],
            admin : [],
        }),
          async created() {
            var temp = await axios.get(axios.fixUrl('/admins'))

            temp.data._embedded.admins.map(obj => obj.id=obj._links.self.href.split("/")[obj._links.self.href.split("/").length - 1])

            this.admin = temp.data._embedded.admins;
        },
        methods: {
        }
    }
</script>

