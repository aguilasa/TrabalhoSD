# TrabalhoSD
Trabalho - Sistemas Distribuídos - FURB

# Descrição do trabalho prático

A aplicação deverá efetuar cópias de segurança dos seus arquivos de
usuário armazenados em um dispositivo móvel, uma vez que tiver acesso
pela rede ao seu computador desktop de casa e/ou do trabalho.

Ao se conectar à rede, o dispositivo móvel deve enviar uma mensagem
para um endereço de multicast com o intuito de localizar os computadores
que possuem o serviço de backup.

Estes devem responder enviando um datagrama no qual será especificado
o endereço IP e a porta do serviço de backup. O dispositivo móvel pode
então estabelecer uma conexão com o serviço de backupe transmitir os
arquivos cujas cópias de segurança serão armazenadas no computador.

Para testar a sua implementação, você pode executar tanto o cliente
(programa que rodaria no dispositivo móvel) quanto o servidor
de backup (programa que rodaria no desktop) no mesmo computador.
