import { defineStore } from "pinia";
import { saveToken, removeToken, getToken } from "@/utils/jwt";
import type { User } from "@/types/User";
import axios from "@/api/axiosInstance";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    user: null as User | null,
    token: "" as string,
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
  },
  actions: {
    login(user: User, token: string) {
      this.user = user;
      this.token = token;
      saveToken(token);
    },

    async logout() {
      try {
        if (this.user?.username) {
          await axios.post("/v1/auth/logout", null, {
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
          });
        }
      } catch (error) {
        console.error("Logout request failed", error);
      } finally {
        this.clearUser();
      }
    },
    clearUser() {
      this.user = null;
      this.token = "";
      removeToken();
    },
    initialize() {
      const token = getToken();
      if (token) {
        this.token = token;

        try {
          const payload = JSON.parse(atob(token.split(".")[1]));
          this.user = {
            id: "",
            username: payload.sub,
            role: { id: "", name: payload.role },
            isActive: payload.isActive,
            createdAt: "",
          };
        } catch (e) {
          console.error("Error of decoding token", e);
        }
      }
    },
  },
});
