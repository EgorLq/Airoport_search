# Airoport_search

Вначале возникла проблема , что не совсем понял как можно не хранить весь файл и не перечитывать его , сделал первоначальную версию 
через стандартные функции , и перечитывал весь файл , тест кейс получился за 80 мс , долго ломал голову , пока не пришла мысль 
что можно не весь файл хранить а лишь столбец по которому идет запрос , и из него вытаскивать нужную нам информацию . 
дальше пошла всякого рода оптимизация , что бы уложиться в время . Можно было бы еще оптимизировать , что бы на тест кейсе выдавало 1 мс , используя
возможности 19 java и виртуальные потоки .
