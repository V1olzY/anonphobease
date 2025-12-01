import { setActivePinia, createPinia } from "pinia";
import { useAuthStore } from "@/stores/authStore";
import axios from "@/api/axiosInstance";
import { saveToken, removeToken, getToken } from "@/utils/jwt";

jest.mock("@/api/axiosInstance", () => ({
  __esModule: true,
  default: {
    post: jest.fn(),
  },
}));

jest.mock("@/utils/jwt", () => ({
  saveToken: jest.fn(),
  removeToken: jest.fn(),
  getToken: jest.fn(),
}));

const mockedAxios = axios as unknown as { post: jest.Mock };

describe("authStore", () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    jest.clearAllMocks();
  });

  it("login sets user and token and calls saveToken", () => {
    const store = useAuthStore();

    const user = {
      id: "u1",
      username: "testuser",
      role: { id: "r1", name: "ADMIN" },
      isActive: true,
      createdAt: "2024-01-01T00:00:00Z",
    };
    const token = "jwt-token";

    store.login(user, token);

    expect(store.user).toEqual(user);
    expect(store.token).toBe(token);
    expect(saveToken).toHaveBeenCalledWith(token);
  });

  it("logout calls /v1/auth/logout and clears the state", async () => {
    const store = useAuthStore();

    store.user = {
      userId: "u1",
      username: "testuser",
      role: { id: "r1", name: "ADMIN" },
    } as any;
    store.token = "jwt-token";

    mockedAxios.post.mockResolvedValue({});

    await store.logout();

    expect(mockedAxios.post).toHaveBeenCalledWith(
      "/v1/auth/logout",
      null,
      expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: `Bearer jwt-token`,
        }),
      })
    );

    expect(store.user).toBeNull();
    expect(store.token).toBe("");
    expect(removeToken).toHaveBeenCalled();
  });

  it("initialize loads token and restores user data from it", () => {
    const store = useAuthStore();

    const payload = { sub: "john", role: "ADMIN" };
    const encodedPayload = JSON.stringify(payload);

    (getToken as jest.Mock).mockReturnValue(
      `header.${encodedPayload}.signature`
    );

    (globalThis as any).atob = (str: string) => str;

    store.initialize();

    expect(store.token).toBe(`header.${encodedPayload}.signature`);
    expect(store.user).not.toBeNull();
    expect(store.user?.username).toBe("john");
    expect(store.user?.role.name).toBe("ADMIN");
  });

  it("isLoggedIn returns true only when token is present", () => {
    const store = useAuthStore();
    expect(store.isLoggedIn).toBe(false);
    store.token = "t";
    expect(store.isLoggedIn).toBe(true);
  });
});
