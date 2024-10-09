<template>
    <v-card style="width:450px; height:100%;" outlined>
        <template slot="progress">
            <v-progress-linear color="primary-darker-1" height="10" indeterminate></v-progress-linear>
        </template>

        <v-card-title v-if="value._links">
            UserID : {{decode(value._links.self.href.split("/")[value._links.self.href.split("/").length - 1])}}
        </v-card-title>
        <v-card-title v-else>
            User를 등록합니다.
        </v-card-title>

        <br>
        <v-card-text style="background-color: white;">
            <label class="v-label">이름</label>
            <String v-model="value.name" :editMode="editMode" :inputUI="''" />
            <br>
            <label class="v-label">포인트</label>
            <Number v-model="value.point" :editMode="editMode" :inputUI="''" />
            <br>
            <label class="v-label">Capacity</label>
            <Number v-model="value.capacity" :editMode="editMode" :inputUI="''" />

        </v-card-text>

        <v-card-actions style="background-color: white;">
            <v-spacer></v-spacer>
            <div>
                <v-btn color="primary" text @click="save" v-if="editMode">
                    등록하기
                </v-btn>
            </div>
        </v-card-actions>
        <v-card-actions>
            <v-spacer></v-spacer>
        </v-card-actions>

        <v-snackbar v-model="snackbar.status" :top="true" :timeout="snackbar.timeout" color="error">
            {{ snackbar.text }}
            <v-btn dark text @click="snackbar.status = false">
                Close
            </v-btn>
        </v-snackbar>
    </v-card>

</template>

<script>
const axios = require('axios').default;


export default {
    name: 'UserUser',
    components: {
    },
    props: {
        value: [Object, String, Number, Boolean, Array],
        editMode: Boolean,
        isNew: Boolean,
        offline: Boolean,
    },
    data: () => ({
        snackbar: {
            status: false,
            timeout: 5000,
            text: '',
        },
    }),
    async created() {
    },
    methods: {
        decode(value) {
            return decodeURIComponent(value);
        },
        selectFile() {
            if (this.editMode == false) {
                return false;
            }
            var me = this
            var input = document.createElement("input");
            input.type = "file";
            input.accept = "image/*";
            input.id = "uploadInput";

            input.click();
            input.onchange = function (event) {
                var file = event.target.files[0]
                var reader = new FileReader();

                reader.onload = function () {
                    var result = reader.result;
                    me.imageUrl = result;
                    me.value.photo = result;

                };
                reader.readAsDataURL(file);
            };
        },
        edit() {
            this.editMode = true;
        },
        async save() {
            try {
                var temp = null;

                if (!this.offline) {
                    if (this.isNew) {
                        temp = await axios.post(axios.fixUrl('/users'), this.value)
                    } else {
                        temp = await axios.put(axios.fixUrl(this.value._links.self.href), this.value)
                    }
                }

                if (this.value != null) {
                    for (var k in temp.data) this.value[k] = temp.data[k];
                } else {
                    this.value = temp.data;
                }

                this.editMode = false;
                this.$emit('input', this.value);

                if (this.isNew) {
                    this.$emit('add', this.value);
                } else {
                    this.$emit('edit', this.value);
                }

                location.reload()

            } catch (e) {
                this.snackbar.status = true
                if (e.response && e.response.data.message) {
                    this.snackbar.text = e.response.data.message
                } else {
                    this.snackbar.text = e
                }
            }

        },
        async remove() {
            try {
                if (!this.offline) {
                    await axios.delete(axios.fixUrl(this.value._links.self.href))
                }

                this.editMode = false;
                this.isDeleted = true;

                this.$emit('input', this.value);
                this.$emit('delete', this.value);

            } catch (e) {
                this.snackbar.status = true
                if (e.response && e.response.data.message) {
                    this.snackbar.text = e.response.data.message
                } else {
                    this.snackbar.text = e
                }
            }
        },
        change() {
            this.$emit('input', this.value);
        },
    },
}
</script>
