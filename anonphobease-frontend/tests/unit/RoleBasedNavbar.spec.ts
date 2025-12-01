import { mount } from "@vue/test-utils";
import { reactive } from "vue";
import RoleBasedNavbar from "@/components/nav/RoleBasedNavbar.vue";

// mock authStore
const authStoreMock = reactive<{ user: any | null; token: string }>({
  user: null,
  token: "",
});

jest.mock("@/stores/authStore", () => ({
  useAuthStore: () => authStoreMock,
}));

// ✅ ВАЖНО: __esModule: true + default: { ... }

jest.mock("@/components/nav/AdminNavbar.vue", () => ({
  __esModule: true,
  default: {
    name: "AdminNavbar",
    template: "<div data-test='admin-navbar' />",
  },
}));

jest.mock("@/components/nav/ModeratorNavbar.vue", () => ({
  __esModule: true,
  default: {
    name: "ModeratorNavbar",
    template: "<div data-test='moderator-navbar' />",
  },
}));

jest.mock("@/components/nav/UserNavbar.vue", () => ({
  __esModule: true,
  default: {
    name: "UserNavbar",
    template: "<div data-test='user-navbar' />",
  },
}));

jest.mock("@/components/common/LanguageSelector.vue", () => ({
  __esModule: true,
  default: {
    name: "LanguageSelector",
    template: "<div data-test='language-selector' />",
  },
}));

jest.mock("@/components/common/LogoutButton.vue", () => ({
  __esModule: true,
  default: {
    name: "LogoutButton",
    template: "<button data-test='logout-button' />",
  },
}));

describe("RoleBasedNavbar.vue", () => {
  beforeEach(() => {
    authStoreMock.user = null;
    authStoreMock.token = "";
  });

  it("shows no navbar and no LogoutButton when not logged in", () => {
    const wrapper = mount(RoleBasedNavbar);

    expect(wrapper.find("[data-test='admin-navbar']").exists()).toBe(false);
    expect(wrapper.find("[data-test='moderator-navbar']").exists()).toBe(false);
    expect(wrapper.find("[data-test='user-navbar']").exists()).toBe(false);

    // LanguageSelector should always be present
    expect(wrapper.find("[data-test='language-selector']").exists()).toBe(true);
    // LogoutButton only when logged in
    expect(wrapper.find("[data-test='logout-button']").exists()).toBe(false);
  });

  it("renders AdminNavbar and LogoutButton for ADMIN role", () => {
    authStoreMock.token = "token";
    authStoreMock.user = { role: { name: "ADMIN" } };

    const wrapper = mount(RoleBasedNavbar);

    expect(wrapper.find("[data-test='admin-navbar']").exists()).toBe(true);
    expect(wrapper.find("[data-test='moderator-navbar']").exists()).toBe(false);
    expect(wrapper.find("[data-test='user-navbar']").exists()).toBe(false);
    expect(wrapper.find("[data-test='logout-button']").exists()).toBe(true);
  });

  it("renders ModeratorNavbar for MODERATOR role", () => {
    authStoreMock.token = "token";
    authStoreMock.user = { role: { name: "MODERATOR" } };

    const wrapper = mount(RoleBasedNavbar);

    expect(wrapper.find("[data-test='moderator-navbar']").exists()).toBe(true);
    expect(wrapper.find("[data-test='admin-navbar']").exists()).toBe(false);
    expect(wrapper.find("[data-test='user-navbar']").exists()).toBe(false);
  });

  it("renders UserNavbar for USER role", () => {
    authStoreMock.token = "token";
    authStoreMock.user = { role: { name: "USER" } };

    const wrapper = mount(RoleBasedNavbar);

    expect(wrapper.find("[data-test='user-navbar']").exists()).toBe(true);
    expect(wrapper.find("[data-test='admin-navbar']").exists()).toBe(false);
    expect(wrapper.find("[data-test='moderator-navbar']").exists()).toBe(false);
  });
});
