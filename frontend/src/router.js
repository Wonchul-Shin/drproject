
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import DrDrManager from "./components/listers/DrDrCards"
import DrDrDetail from "./components/listers/DrDrDetail"

import ResponseResponseManager from "./components/listers/ResponseResponseCards"
import ResponseResponseDetail from "./components/listers/ResponseResponseDetail"

import UserUserManager from "./components/listers/UserUserCards"
import UserUserDetail from "./components/listers/UserUserDetail"

import KepcoKepcoManager from "./components/listers/KepcoKepcoCards"
import KepcoKepcoDetail from "./components/listers/KepcoKepcoDetail"


export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/drs/drs',
                name: 'DrDrManager',
                component: DrDrManager
            },
            {
                path: '/drs/drs/:id',
                name: 'DrDrDetail',
                component: DrDrDetail
            },

            {
                path: '/responses/responses',
                name: 'ResponseResponseManager',
                component: ResponseResponseManager
            },
            {
                path: '/responses/responses/:id',
                name: 'ResponseResponseDetail',
                component: ResponseResponseDetail
            },

            {
                path: '/users/users',
                name: 'UserUserManager',
                component: UserUserManager
            },
            {
                path: '/users/users/:id',
                name: 'UserUserDetail',
                component: UserUserDetail
            },

            {
                path: '/kepcos/kepcos',
                name: 'KepcoKepcoManager',
                component: KepcoKepcoManager
            },
            {
                path: '/kepcos/kepcos/:id',
                name: 'KepcoKepcoDetail',
                component: KepcoKepcoDetail
            },



    ]
})
