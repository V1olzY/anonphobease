import {
  createRouter,
  createWebHistory,
  RouteRecordRaw,
  NavigationGuardNext,
  RouteLocationNormalized,
} from "vue-router";
import { useAuthStore } from "@/stores/authStore";

import LoginView from "@/views/LoginView.vue";
import UsersView from "@/views/admin/UsersView.vue";
import RolesView from "@/views/admin/RolesView.vue";
import ReportsView from "@/views/ReportsView.vue";
import LogsView from "@/views/admin/LogsView.vue";
import ChatsView from "@/views/ChatsView.vue";
import LanguagesView from "@/views/admin/LanguagesView.vue";
import BansView from "@/views/BansView.vue";
import PhobiasView from "@/views/admin/PhobiasView.vue";
import AdminChatsView from "@/views/admin/AdminChatsView.vue";
import ChatView from "@/views/ChatView.vue";
import userLogsView from "@/views/admin/UserLogsView.vue";

const routes: RouteRecordRaw[] = [
  {
    path: "/",
    name: "Login",
    component: LoginView,
  },
  {
    path: "/users",
    name: "Users",
    component: UsersView,
    meta: { requiresAuth: true, roles: ["ADMIN"] },
  },
  {
    path: "/roles",
    name: "Roles",
    component: RolesView,
    meta: { requiresAuth: true, roles: ["ADMIN"] },
  },
  {
    path: "/reports",
    name: "Reports",
    component: ReportsView,
    meta: { requiresAuth: true, roles: ["ADMIN", "MODERATOR"] },
  },
  {
    path: "/logs",
    name: "Logs",
    component: LogsView,
    meta: { requiresAuth: true, roles: ["ADMIN", "MODERATOR"] },
  },
  {
    path: "/chats",
    name: "Chats",
    component: ChatsView,
    meta: { requiresAuth: true, roles: ["USER", "MODERATOR", "ADMIN"] },
  },
  {
    path: "/chats/:id",
    name: "Chat",
    component: ChatView,
    meta: { requiresAuth: true, roles: ["USER", "MODERATOR", "ADMIN"] },
  },
  {
    path: "/admin/chats",
    name: "AdminChats",
    component: AdminChatsView,
    meta: { requiresAuth: true, roles: ["ADMIN"] },
  },
  {
    path: "/languages",
    name: "Languages",
    component: LanguagesView,
    meta: { requiresAuth: true, roles: ["ADMIN"] },
  },
  {
    path: "/bans",
    name: "Bans",
    component: BansView,
    meta: { requiresAuth: true, roles: ["MODERATOR", "ADMIN"] },
  },
  {
    path: "/phobias",
    name: "Phobias",
    component: PhobiasView,
    meta: { requiresAuth: true, roles: ["ADMIN"] },
  },
  {
    path: "/logs/:id",
    name: "userLog",
    component: userLogsView,
    meta: { requiresAuth: true, roles: ["ADMIN"] },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(
  (
    to: RouteLocationNormalized,
    from: RouteLocationNormalized,
    next: NavigationGuardNext
  ) => {
    const authStore = useAuthStore();
    const isLoggedIn = !!authStore.token;

    if (to.path === "/" && isLoggedIn) {
      const role = authStore.user?.role.name;
      if (role === "ADMIN") return next("/users");
      if (role === "MODERATOR") return next("/reports");
      return next("/chats");
    }

    if (to.meta.requiresAuth && !isLoggedIn) {
      return next("/");
    }

    const allowedRoles = to.meta.roles as string[] | undefined;
    const userRole = authStore.user?.role.name;

    if (allowedRoles && !allowedRoles.includes(userRole || "")) {
      return next("/");
    }

    next();
  }
);

export default router;
