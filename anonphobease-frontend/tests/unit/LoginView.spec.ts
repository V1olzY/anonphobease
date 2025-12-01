import { mount, flushPromises } from "@vue/test-utils";
import LoginView from "@/views/LoginView.vue";
import axios from "@/api/axiosInstance";

// Mock authStore
const loginMock = jest.fn();
jest.mock("@/stores/authStore", () => ({
  useAuthStore: () => ({
    login: loginMock,
  }),
}));

// Mock axios
jest.mock("@/api/axiosInstance", () => ({
  __esModule: true,
  default: {
    get: jest.fn(),
    post: jest.fn(),
  },
}));

// Mock router
const pushMock = jest.fn();
jest.mock("vue-router", () => ({
  useRouter: () => ({
    push: pushMock,
  }),
}));

// Mock i18n
jest.mock("vue-i18n", () => ({
  useI18n: () => ({
    t: (key: string) => key,
  }),
}));

const mockedAxios = axios as unknown as {
  get: jest.Mock;
  post: jest.Mock;
};

describe("LoginView.vue", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const mountLogin = () =>
    mount(LoginView, {
      global: {
        mocks: {
          $t: (key: string) => key,
        },
      },
    });

  it("does not attempt login when username is empty", async () => {
    const wrapper = mountLogin();

    const button = wrapper.get("button");
    await button.trigger("click");
    await flushPromises();

    expect(mockedAxios.post).not.toHaveBeenCalled();
    expect(loginMock).not.toHaveBeenCalled();
  });

  it("checkUsername calls /v1/auth/check and enables requiresPassword", async () => {
    const wrapper = mountLogin();

    mockedAxios.get.mockResolvedValue({
      data: { requiresPassword: true },
    });

    const usernameInput = wrapper.get("input");
    await usernameInput.setValue("john");
    await usernameInput.trigger("blur");

    await flushPromises();

    expect(mockedAxios.get).toHaveBeenCalledWith("/v1/auth/check", {
      params: { nickname: "john" },
    });

    expect(wrapper.find('input[type="password"]').exists()).toBe(true);
  });

  it("successful login calls authStore.login and router.push based on role", async () => {
    const wrapper = mountLogin();

    mockedAxios.get.mockResolvedValue({
      data: { requiresPassword: false },
    });

    mockedAxios.post.mockResolvedValue({
      data: {
        token: "jwt-token",
        username: "john",
        role: "ADMIN",
        userId: "u1",
        roleId: "r1",
      },
    });

    const usernameInput = wrapper.get("input");
    await usernameInput.setValue("john");
    await usernameInput.trigger("blur");
    await flushPromises();

    const button = wrapper.get("button");
    await button.trigger("click");
    await flushPromises();

    expect(mockedAxios.post).toHaveBeenCalledWith("/v1/auth/login", {
      username: "john",
    });

    expect(loginMock).toHaveBeenCalledWith(
      { userId: "u1", username: "john", role: { id: "r1", name: "ADMIN" } },
      "jwt-token"
    );

    expect(pushMock).toHaveBeenCalledWith("/users");
  });
});
