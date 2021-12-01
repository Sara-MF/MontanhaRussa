# Descrição do problema

O problema da montanha russa é descrito da seguinte forma: existe a montanha russa, que é o processo main que controlará os demais processos, e os passageiros e os carrinhos, que serão os processos concorrentes: os passageiros irão solicitar e esperar para entrar no carrinho e o carrinho terá que esperar os passageiros embarcarem e ter cautela para não colidir em outros carrinhos durante o percurso.

# Casos

Existem 3 casos para esse problema:

Caso 1: existe apenas 1 carrinho e o total de 52 passageiros

Caso 2: existem 2 carrinhos e o total de 92 passageiros

Caso 3: existem 3 carrinhos e o total de 148 passageiros

# Restrições

Em todos os casos existem algumas restrições:

O tempo que os passageiros chegam na fila é aleatório (entre 1 segundo e 3 segundos)

O carrinho só pode sair para o passeio quando tiver exatamente 4 passageiros embarcados (capacidade total do carrinho)

O tempo que todos os passageiros levam para embarcar e desembarcar do carrinho é de 1 segundo (1 segundo para embarcar e 1 segundo para desembarcar)

Cada volta na montanha russa dura 10 segundos

No fim de cada caso era preciso apresentar os tempos em que os passageiros ficaram esperando na fila (o tempo mínimo, o máximo e o médio) e o tempo em que os carrinhos estavam sendo utilizados (tempo das voltas que deram na montanha russa)




