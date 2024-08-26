using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using WebSocketSharp;

namespace WindowsAppControl
{
    public partial class Form1 : Form
    {
        private WebSocket _webSocket;

        public Form1()
        {
            InitializeComponent();
            InitializeWebSocket();
        }

        private void InitializeWebSocket()
        {
            string serverUrl = Environment.GetEnvironmentVariable("SERVER_URL"); // "ws://0.0.0.0:8080";
            _webSocket = new WebSocket(serverUrl);

            _webSocket.OnOpen += (sender, e) =>
            {
                // Envie a autenticação quando a conexão estiver aberta
                SendAuthentication();
            };

            _webSocket.OnMessage += (sender, e) =>
            {
                Invoke((MethodInvoker)(() => OnMessageReceived(e.Data)));
            };

            _webSocket.OnError += (sender, e) =>
            {
                Invoke((MethodInvoker)(() => OnMessageReceived($"Error: {e.Message}")));
            };

            _webSocket.OnClose += (sender, e) =>
            {
                if (this.IsHandleCreated)
                {
                    Invoke((MethodInvoker)(() => OnMessageReceived("Connection closed")));
                }
            };

            _webSocket.Connect();
        }

        private void SendAuthentication()
        {
            string token = Environment.GetEnvironmentVariable("TOKEN");
            string authMessage = $"{{\"token\": \"{token}\"}}";
            if (_webSocket.ReadyState == WebSocketState.Open)
            {
                _webSocket.Send(authMessage);
            }
            else
            {
                // Tratar o caso onde o WebSocket não está aberto
                Invoke((MethodInvoker)(() => OnMessageReceived("WebSocket is not open. Failed to send authentication.")));
            }
        }

        private void OnMessageReceived(string message)
        {
            if (this.InvokeRequired)
            {
                this.Invoke((MethodInvoker)(() => textBoxOutput.AppendText($"{message}\r\n")));
            }
            else
            {
                textBoxOutput.AppendText($"{message}\r\n");
            }
        }

        private void buttonSendCommand_Click(object sender, EventArgs e)
        {
            string command = textBoxCommand.Text;
            if (!string.IsNullOrEmpty(command))
            {
                _webSocket.Send(command);
            }
        }

        private void Form1_FormClosing(object sender, FormClosingEventArgs e)
        {
            _webSocket.Close();
        }
    }
}
