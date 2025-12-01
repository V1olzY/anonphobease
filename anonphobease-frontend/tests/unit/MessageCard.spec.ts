import { mount } from "@vue/test-utils";
import MessageCard from "@/components/chat/MessageCard.vue";

describe("MessageCard.vue", () => {
  it("renders own message with 'self' class and without report button", () => {
    const wrapper = mount(MessageCard, {
      props: {
        username: "me",
        message: "Hello",
        isSelf: true,
        role: "USER",
      },
    });

    const root = wrapper.get(".message-card");
    expect(root.classes()).toContain("self");
    expect(root.classes()).not.toContain("other");

    // кнопки репорта быть не должно
    expect(wrapper.find(".report-button").exists()).toBe(false);
  });

  it("renders other user's message with report button when role is USER", async () => {
    const onReport = jest.fn();

    const wrapper = mount(MessageCard, {
      props: {
        username: "other",
        message: "Hi",
        isSelf: false,
        role: "USER",
        onReport,
      },
    });

    const root = wrapper.get(".message-card");
    expect(root.classes()).toContain("other");
    expect(root.classes()).not.toContain("self");

    const reportButton = wrapper.get(".report-button");
    await reportButton.trigger("click");
    expect(onReport).toHaveBeenCalledTimes(1);
  });

  it("applies correct CSS class for ADMIN role", () => {
    const wrapper = mount(MessageCard, {
      props: {
        username: "adminUser",
        message: "Admin message",
        isSelf: false,
        role: "ADMIN",
      },
    });

    const usernameSpan = wrapper.get(".username");
    expect(usernameSpan.classes()).toContain("admin");
  });

  it("applies correct CSS class for MODERATOR role", () => {
    const wrapper = mount(MessageCard, {
      props: {
        username: "modUser",
        message: "Mod message",
        isSelf: false,
        role: "MODERATOR",
      },
    });

    const usernameSpan = wrapper.get(".username");
    expect(usernameSpan.classes()).toContain("moderator");
  });
});
