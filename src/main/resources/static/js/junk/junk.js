document.addEventListener('DOMContentLoaded', () => {
    const registrationForm = document.getElementById('registrationForm');
    const loginForm = document.getElementById('loginForm');
    const mainChatSection = document.getElementById('mainChatSection');
    const registrationFormLoginBtn = document.getElementById('registrationFormLoginBtn');
    const loginFormRegisterBtn = document.getElementById('loginFormRegisterBtn');
    const chatContainerAddGroup = document.getElementById('chatContainerAddGroup');
    const sentMessage = document.getElementById('sendMessage');

    let currentUser = null;
    let currentGroup = null;



    const stompClient = new StompJs.Client({
        brokerURL: 'ws://localhost:8080/ws'
    });

    stompClient.onConnect = (frame) => {
        console.log('Connected: ' + frame);
    };

    stompClient.onWebSocketError = (error) => {
        console.error('Error with websocket', error);
    };
    stompClient.activate();
    stompClient.onStompError = (frame) => {
        console.error('Broker reported error: ' + frame.headers['message']);
        console.error('Additional details: ' + frame.body);
    };

    console.log("DOM loaded...");




    // Initially remove the `required` attribute from login form fields
    document.getElementById('emailLoginForm').removeAttribute('required');
    document.getElementById('passwordLoginForm').removeAttribute('required');

    registrationFormLoginBtn.addEventListener('click', ()=>{
        console.log("a tag registrationFormLoginBtn clicked...");
        registrationForm.style.display = 'none';
        mainChatSection.style.display = 'none';
        loginForm.style.display = 'block';
    })
    loginFormRegisterBtn.addEventListener('click', ()=>{
        console.log("a tag loginFormLoginBtn clicked...");
        registrationForm.style.display = 'block';
        mainChatSection.style.display = 'none';
        loginForm.style.display = 'none';
    })

    chatContainerAddGroup.addEventListener('click', ()=>{
        console.log("Adding group process has begun...");

    })

    // Handle registration form submission
    document.getElementById('registrationForm').addEventListener('submit', (event) => {
        event.preventDefault();
        console.log("Registration process has begun....")

        const firstName = document.getElementById('firstName').value.trim();
        const lastName = document.getElementById('lastName').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value.trim();

        let isValid = true;
        let errorMessage = '';

        // Validation
        if (!firstName || !lastName) {
            errorMessage += 'First name and last name are required.\n';
            isValid = false;
        }

        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(email)) {
            errorMessage += 'Please enter a valid email address.\n';
            isValid = false;
        }

        const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[\W_]).{8,}$/;
        if (!passwordRegex.test(password)) {
            errorMessage += 'Password must be at least 8 characters long and include uppercase, lowercase, numbers, and special characters.\n';
            isValid = false;
        }

        if (isValid) {
            const url = '/api/v1/users';
            const requestBody = { firstName, lastName, email, password };

            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Error adding user: ${response.statusText}`);
                    }
                    registrationForm.style.display = 'none';
                    loginForm.style.display = 'block'; // Show login form after registration
                    document.getElementById('emailLoginForm').setAttribute('required', '');
                    document.getElementById('passwordLoginForm').setAttribute('required', '');

                    return response.json();
                })
                .then(user => {
                    console.log(`User added successfully: ${user.firstName} ${user.lastName}`);
                    alert('User registered successfully!');
                })
                .catch(error => {
                    console.error('Error adding user:', error);
                    alert('Failed to register user. Please try again.');
                });
        } else {
            alert(errorMessage);
        }
    });


    // Handle login form submission
    loginForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const email = document.getElementById('emailLoginForm').value;
        const password = document.getElementById('passwordLoginForm').value;

        const url = '/api/v1/users/login';
        fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({email, password})
        })
            .then(response => {
                console.log('Response:', response);
                if (!response.ok) {
                    loginForm.style.display = 'block';
                    throw new Error(`Login failed: ${response.statusText}`);
                }
                registrationForm.style.display = 'none';
                loginForm.style.display = 'none';
                mainChatSection.style.display = 'flex'; // Show main chat section after successful login
                return response.json();
            })
            .then(user => {
                console.log('User object:', user);

                if (user.firstName && user.lastName) {
                    currentUser = user;
                    console.log(`User logged in successfully: ${user.firstName} ${user.lastName}`);
                    alert('User logged in successfully!');
                    document.getElementById('userName').textContent = user.firstName;

                    // Render user's groups
                    const groupList = document.getElementById('groupList');
                    const userString = JSON.stringify(user);
                    const parsedUser = JSON.parse(userString);
                    console.log(" parsedUser Object" + parsedUser);
                    // Clear existing group list to avoid duplicates
                    groupList.innerHTML = '';
                    if (Array.isArray(user.chatGroups)) {
                        const groupsHTML = user.chatGroups.map(group => `
                            <li class="group" data-group-id="${group.id}">
                                <h2>${group.groupName}</h2>
                            </li>
                        `).join('');
                        document.getElementById('groupList').innerHTML = groupsHTML; // Efficiently append all groups

                        // Subscribe to each group
                        // user.chatGroups.forEach(group => {
                        //     stompClient.subscribe(`/topic/group/${group.id}`, message => {
                        //         console.log(`Subscription to group ${group.groupName}`);
                        //     },
                        //         error => {
                        //             console.error('Subscription error:', error);
                        //         });
                        //     console.log(`Subscribed to group ${group.groupName} with ID ${group.id}`);
                        // });

                    } else {
                        console.error('User chatGroups is not an array');
                    }

                    // Step 2: Set up event delegation to capture clicks on any group item
                    document.getElementById('groupList').addEventListener('click', (event) => {
                        const groupElement = event.target.closest('.group'); // Get the closest .group element
                        if (groupElement) {
                            const groupId = parseInt(groupElement.getAttribute('data-group-id'), 10);

                            // Find the corresponding group in user.chatGroups by ID
                            const group = user.chatGroups.find(g => g.id === groupId);

                            if (group) {
                                currentGroup = group;
                                populateMessages(group); // Display messages for the selected group
                            } else {
                                console.error('Group not found with ID:', groupId);
                            }
                        } else {
                            throw new Error('User details missing from response.');
                        }
                    });
                }
            })
    });
    function populateMessages(group) {
        const messageList = document.getElementById('messageList');
        messageList.innerHTML = ''; // Clear existing messages
        console.log(`Displaying messages for group: ${group.groupName}`, group.messages);

        if (Array.isArray(group.messages)) {
            group.messages.forEach(message => {
                const messageHTML = `
                  <li class="message">
                    <strong>${message.sender}:</strong> ${message.content} <span class="text-gray-500">${new Date(message.createdAt).toLocaleString()}</span>
                  </li>
                `;
                messageList.innerHTML += messageHTML;
            });
        } else {
            console.error('No messages available for this group.');
        }
    }
    sentMessage.addEventListener("click", ()=> sendMessageToGroup(currentGroup, currentUser))

    function sendMessageToGroup(currentGroup, user) {
        const messageInput = document.getElementById('messageInput');
        const messageContent = messageInput.value.trim();


        if (messageContent && currentGroup.id) {
            const messagePayload = {
                content: messageContent,
                senderId: parseInt(user.id), // Assuming you have the logged-in user's ID
                groupId: parseInt(currentGroup.id), // Include the group ID
                messageType: "CHAT"
            };
            console.log(`Current user   ${user.lastName}  ${currentGroup.groupName}`);
            // console.log(stompClient);
            console.log(`${currentGroup.id}`)
            // console.log(JSON.stringify(currentUser))
            // Use WebSocket to send message

            stompClient.publish({destination: `/app/chat/send`,
                body: JSON.stringify(messagePayload)});

            console.log("Message successfully send ")
            // Clear the input field after sending
            messageInput.value = '';
        }
    }

    function joinGroup(groupId, userId){
        const messagePayload = {
            userId: parseInt(userId), // Assuming you have the logged-in user's ID
            groupId: parseInt(groupId) // Include the group ID
        };
        stompClient.publish({destination: `/app/chat/join`,
            body: JSON.stringify(messagePayload)});

        console.log("User successfully joined group")
    }





});


