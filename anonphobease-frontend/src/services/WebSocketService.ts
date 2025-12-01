class WebSocketService {
  private socket: WebSocket | null = null;

  connect(chatId: string, onMessage: (msg: any) => void) {
    this.socket = new WebSocket(`ws://localhost:8081/ws?chatId=${chatId}`);

    this.socket.onopen = () => {
      console.log("✅ WebSocket connected");
    };

    this.socket.onmessage = (event) => {
      const message = JSON.parse(event.data);
      onMessage(message);
    };

    this.socket.onerror = (error) => {
      console.error("❌ WebSocket error", error);
    };

    this.socket.onclose = () => {
      console.log("🔌 WebSocket disconnected");
    };
  }

  sendMessage(message: any) {
    if (this.socket?.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(message));
    } else {
      console.warn("WebSocket not connected.");
    }
  }

  disconnect() {
    this.socket?.close();
    this.socket = null;
  }
}
