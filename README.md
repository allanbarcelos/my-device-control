# MyDeviceControl

Português | [English](README.en.md)

MyDeviceControl é um conjunto de aplicativos para controlar remotamente dispositivos Android a partir de um cliente Windows, utilizando WebSocket para comunicação em tempo real. O projeto consiste em um aplicativo Android que captura a tela e responde a comandos recebidos de um servidor central, e um cliente Windows que envia comandos e exibe a tela capturada.

## Visão Geral do Projeto

### Componentes do Projeto

1. **Servidor Central**: Recebe dados do dispositivo Android e repassa comandos do cliente Windows para o dispositivo Android.
2. **App Android**: Captura a tela do dispositivo, envia para o servidor e executa comandos recebidos.
3. **App Windows**: Conecta-se ao servidor, envia comandos para o dispositivo Android e exibe a tela capturada.

### Tecnologias Utilizadas

- **Servidor**: Node.js, WebSocket, dotenv.
- **App Android**: Kotlin, WebSocket (via OkHttp), Serviços de Captura de Tela.
- **App Windows**: C#, WinForms, WebSocket (via WebSocketSharp).

## Configuração do Ambiente

### Servidor Central

1. Clone o repositório do servidor central.
2. Instale as dependências usando `npm install`.
3. Crie um arquivo `.env` na raiz do projeto com os seguintes conteúdos:
    ```dotenv
    ANDROID_TOKEN=YOUR_ANDROID_TOKEN
    WINDOWS_TOKEN=YOUR_WINDOWS_TOKEN
    ```
4. Inicie o servidor com `node server.js`.

### App Android

1. Clone o repositório do aplicativo Android.
2. Configure o `build.gradle.kts` (Module: app) para incluir as dependências necessárias.
3. Adicione as seguintes variáveis de ambiente no `build.gradle.kts`:
    ```kotlin
    defaultConfig {
        buildConfigField("String", "ANDROID_TOKEN", "\"YOUR_ANDROID_TOKEN\"")
        buildConfigField("String", "SERVER_API", "\"YOUR_SERVER_URL\"")
    }
    ```
4. Compile e instale o aplicativo no dispositivo Android.

### App Windows

1. Clone o repositório do cliente Windows.
2. Configure as variáveis de ambiente `TOKEN` e `SERVER_URL` no ambiente do Windows.
3. Compile e execute o aplicativo.

## Uso

### App Android

1. Abra o aplicativo Android.
2. O aplicativo automaticamente se conectará ao servidor e começará a capturar a tela e a enviar para o servidor.

### App Windows

1. Abra o aplicativo Windows.
2. O aplicativo se conectará ao servidor, enviará o token para autenticação e exibirá a tela capturada do dispositivo Android.
3. Use o teclado e o mouse para enviar comandos ao dispositivo Android.

## Dependências

### Servidor

- Node.js
- WebSocket
- dotenv

### App Android

- Kotlin
- OkHttp
- AndroidX

### App Windows

- .NET Framework
- WebSocketSharp

## Contribuições

Este projeto é um exemplo básico de controle remoto entre dispositivos Android e Windows. Contribuições para melhorias, adição de funcionalidades ou correção de bugs são bem-vindas!