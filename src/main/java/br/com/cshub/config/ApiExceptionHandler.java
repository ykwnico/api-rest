package br.com.cshub.config;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// Centraliza o tratamento de erros da API, devolvendo sempre um JSON padronizado:
// { "timestamp": "...", "status": 400, "erro": "Bad Request", "mensagem": "...", "campos": { ... } }
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    // 400 - falha nas validações (@NotBlank, @Min, @Pattern...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(erro -> campos.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        Map<String, Object> corpo = corpo(HttpStatus.BAD_REQUEST, "Existem campos inválidos. Verifique os dados enviados.");
        corpo.put("campos", campos);
        return ResponseEntity.badRequest().body(corpo);
    }

    // 400 - JSON mal formatado ou tipo errado (ex.: texto em campo numérico)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(corpo(HttpStatus.BAD_REQUEST, "JSON inválido ou com tipos de dados incorretos."));
    }

    // 400 - ID na URL que não é número (ex.: /api/times/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> parametroInvalido(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(corpo(HttpStatus.BAD_REQUEST, "Parâmetro '" + ex.getName() + "' com valor inválido: " + ex.getValue()));
    }

    // 404, 409, 400... lançados nos controllers com ResponseStatusException
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> status(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(corpo(ex.getStatusCode(), ex.getReason()));
    }

    // 404 - rota inexistente
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> rotaInexistente(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(corpo(HttpStatus.NOT_FOUND, "Recurso não encontrado: /" + ex.getResourcePath()));
    }

    // 405 - método HTTP não suportado na rota
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> metodoNaoSuportado(HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(corpo(HttpStatus.METHOD_NOT_ALLOWED, "Método " + ex.getMethod() + " não é permitido nesta rota."));
    }

    // 409 - violação de restrição do banco (ex.: valor único duplicado)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> integridade(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(corpo(HttpStatus.CONFLICT, "A operação viola uma regra do banco de dados (registro duplicado ou em uso)."));
    }

    // 500 - qualquer outro erro inesperado (detalhes só no log do servidor)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> erroInterno(Exception ex) {
        log.error("Erro interno não tratado", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(corpo(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor. Tente novamente mais tarde."));
    }

    private Map<String, Object> corpo(HttpStatusCode status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", LocalDateTime.now().toString());
        corpo.put("status", status.value());
        HttpStatus conhecido = HttpStatus.resolve(status.value());
        corpo.put("erro", conhecido != null ? conhecido.getReasonPhrase() : "Erro");
        corpo.put("mensagem", mensagem);
        return corpo;
    }
}
