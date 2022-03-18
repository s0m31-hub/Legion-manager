# Legion manager

Бот на заказ для https://vk.com/alegions  

## Как развернуть
### Схема базы данных
Схема: public  
-> Стол: chat    
--> Строки: id (int, nn, pk), name (character varying, nn), members (text, nn)  
  
-> Cтол: test  
--> Строки: id (int, nn, pk), ok (boolean, nn, default: true)  
  
    
Схема: users  
-> Стол: user  
--> Строки:  id (int, nn, pk), name (character varying, nn), link (character varying, nn), banned (boolean, nn, default: false), rank (int, nn, default: 0)
