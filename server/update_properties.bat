@echo off
copy /Y "%~dp0src\main\resources\application_new.properties" "%~dp0src\main\resources\application.properties"
del "%~dp0src\main\resources\application_new.properties"
echo Arquivo application.properties atualizado com sucesso!
