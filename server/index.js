require('dotenv').config();
const WebSocket = require('ws');

// Carregando os tokens de autenticação do arquivo .env
const VALID_TOKENS = {
    "android": process.env.ANDROID_TOKEN,
    "windows": process.env.WINDOWS_TOKEN
};

const wss = new WebSocket.Server({ port: process.env.PORT });

let androidClient = null;
let windowsClient = null;

wss.on('connection', function connection(ws) {
    let authenticated = false;
    let clientType = null;

    ws.on('message', function incoming(message) {
        try {
            const data = JSON.parse(message);

            // Autenticação
            if (!authenticated) {
                if (data.token && (data.token === VALID_TOKENS.android || data.token === VALID_TOKENS.windows)) {
                    authenticated = true;
                    clientType = data.token === VALID_TOKENS.android ? 'android' : 'windows';

                    if (clientType === 'android') {
                        androidClient = ws;
                    } else if (clientType === 'windows') {
                        windowsClient = ws;
                    }

                    console.log(`${clientType} conectado e autenticado.`);
                    ws.send(JSON.stringify({ status: 'authenticated' }));
                } else {
                    console.log('Autenticação falhou. Conexão encerrada.');
                    ws.send(JSON.stringify({ status: 'authentication_failed' }));
                    ws.close();
                }
                return;
            }

            // Após autenticação, processa as mensagens
            if (data.type === 'screen' && clientType === 'android') {
                if (windowsClient) {
                    windowsClient.send(message); // Repassa a tela para o Windows
                }
            } else if (data.type === 'command' && clientType === 'windows') {
                if (androidClient) {
                    androidClient.send(message); // Repassa o comando para o Android
                }
            }
        } catch (error) {
            console.error('Erro ao processar mensagem:', error);
        }
    });

    ws.on('close', () => {
        console.log(`${clientType} desconectado.`);
        if (ws === androidClient) androidClient = null;
        if (ws === windowsClient) windowsClient = null;
    });
});

console.log(`Servidor WebSocket rodando na porta ${process.env.PORT}`);
