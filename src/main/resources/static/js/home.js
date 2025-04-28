const openChatbot = async () =>{
    await initAgent();
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

const sendMessage = async ()=>{
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

        await callMessage(message);

    }
};

const initAgent = async () =>{
    try {
        const response = await axios({
            method: 'post',
            url: '/home/agentInit',
        });
        console.log(response);
    } catch (error) {
        console.error('error'+error);
    }
};

const enterMessage = (event) =>{
    if(event.keyCode === 13) {
        sendMessage();
    }
};

const callMessage = async (message) => {
    try {
        const response = await axios({
            method: 'post',
            url: '/home/sentMessage',
            data: {
                message: message,
            }
        });
        const userMessage = document.createElement('div');
        userMessage.classList.add('chatbot-message', 'bot');
        userMessage.innerHTML = `<img src="/images/bot.png" alt="User Avatar"><span>${response.data}</span>`;

        // Append the message to the chatbot body
        document.getElementById('chatbotBody').appendChild(userMessage);

        // Scroll to the latest message
        document.getElementById('chatbotBody').scrollTop = document.getElementById('chatbotBody').scrollHeight;
    } catch (error) {
        console.error('error'+error);
    }
}