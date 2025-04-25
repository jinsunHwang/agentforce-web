const openChatbot = () =>{
    toggleChatbot();
};

const toggleChatbot = ()=>{
    const chatbotWindow = document.getElementById("chatbot-window");
    if (chatbotWindow.style.display === "none" || chatbotWindow.style.display === "") {
        chatbotWindow.style.display = "block"; // 챗봇 창 보이기
    } else {
        chatbotWindow.style.display = "none"; // 챗봇 창 숨기기
    }
};

const sendMessage = ()=>{
    const inputField = document.getElementById('userInput');
    const message = inputField.value;
    if (message.trim() !== "") {
        // Create user message element
        const userMessage = document.createElement('div');
        userMessage.classList.add('chatbot-message', 'user');
        userMessage.innerHTML = `<img src="/images/user.png" alt="User Avatar"><span>${message}</span>`;

        // Append the message to the chatbot body
        document.getElementById('chatbotBody').appendChild(userMessage);

        // Scroll to the latest message
        document.getElementById('chatbotBody').scrollTop = document.getElementById('chatbotBody').scrollHeight;

        // Clear input field
        inputField.value = '';
    }
};

const enterMessage = (event) =>{
    if(event.keyCode === 13) {
        sendMessage();
    }
};