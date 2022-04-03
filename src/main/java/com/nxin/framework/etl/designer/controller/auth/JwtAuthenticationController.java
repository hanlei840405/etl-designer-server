package com.nxin.framework.etl.designer.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nxin.framework.etl.designer.dto.auth.AuthDto;
import com.nxin.framework.etl.designer.entity.basic.User;
import com.nxin.framework.etl.designer.enums.Constant;
import com.nxin.framework.etl.designer.exception.UsernameExistedException;
import com.nxin.framework.etl.designer.jwt.JwtTokenUtil;
import com.nxin.framework.etl.designer.jwt.JwtUserDetailsService;
import com.nxin.framework.etl.designer.jwt.JwtUtil;
import com.nxin.framework.etl.designer.jwt.RegisterForm;
import com.nxin.framework.etl.designer.service.basic.UserService;
import com.nxin.framework.etl.designer.vo.auth.AuthVo;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class JwtAuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Value("${spring.mail.from}")
    private String mailFrom;
    @Value("${spring.mail.forgotSubject}")
    private String forgotSubject;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping(value = "/register")
    public ResponseEntity<AuthVo> register(@RequestBody RegisterForm registerForm) {
        try {
            log.info("registerForm: {}", objectMapper.writeValueAsString(registerForm));
            User
                    persist = userService.one(registerForm.getEmail());
            if (persist != null) {
                return ResponseEntity.status(Constant.EXCEPTION_EMAIL_EXISTED).build();
            }
            User user = jwtUserDetailsService.register(registerForm);
            final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getEmail());
            return ResponseEntity.ok(AuthVo.builder()
                    .token(jwtTokenUtil.generateToken(userDetails))
                    .username(userDetails.getUsername())
                    .authorities(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())).build());
        } catch (UsernameExistedException e) {
            return ResponseEntity.status(Constant.EXCEPTION_EMAIL_EXISTED).build();
        } catch (DisabledException e) {
            return ResponseEntity.status(Constant.EXCEPTION_DISABLED).build();
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(Constant.EXCEPTION_BAD_CREDENTIALS).build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResponseEntity.status(Constant.EXCEPTION_DISABLED).build();
        }

    }

    @PostMapping(value = "/authenticate")
    public ResponseEntity<AuthVo> authenticate(@RequestBody AuthDto authDto) {
        try {
            log.info("username: {}", authDto.getUsername());
            authenticate(authDto.getUsername(), authDto.getPassword());
        } catch (DisabledException e) {
            return ResponseEntity.status(Constant.EXCEPTION_DISABLED).build();
        } catch (BadCredentialsException e) {
            log.info("BadCredentialsException: {}", e.toString());
            return ResponseEntity.status(Constant.EXCEPTION_BAD_CREDENTIALS).build();
        }
        final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(authDto.getUsername());
        return ResponseEntity.ok(AuthVo.builder()
                .token(jwtTokenUtil.generateToken(userDetails))
                .username(userDetails.getUsername())
                .authorities(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())).build());
    }

    @GetMapping(value = "/refresh")
    public ResponseEntity<AuthVo> refresh(HttpServletRequest request) throws Exception {
        DefaultClaims defaultClaims = (io.jsonwebtoken.impl.DefaultClaims) request.getAttribute("claims");
        Map<String, Object> expectedMap = getMapFromIoJsonWebTokenClaims(defaultClaims);
        return ResponseEntity.ok(AuthVo.builder().token(jwtUtil.doGenerateRefreshToken(expectedMap, defaultClaims.getSubject())).build());
    }

    @PostMapping("/forgot")
    public ResponseEntity<?> forgot(@RequestBody AuthDto authDto) {
        User user = userService.one(authDto.getEmail());
        if (user == null) {
            return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
        }
        if (!user.getName().equals(authDto.getName())) {
            return ResponseEntity.status(Constant.EXCEPTION_RECORDS_NOT_MATCH).build();
        }
        String code = String.format("%04d", new Random().nextInt(9999));
        stringRedisTemplate.opsForValue().set("verification/" + user.getEmail(), code, 3600L, TimeUnit.SECONDS);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.getEmail());
        message.setSubject(forgotSubject);
        message.setText("验证码为: " + code);
        javaMailSender.send(message);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity<AuthVo> reset(@RequestBody AuthDto authDto) {
        User user = userService.one(authDto.getEmail());
        if (user == null) {
            return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
        }
        String code = stringRedisTemplate.opsForValue().get("verification/" + user.getEmail());
        if (StringUtils.hasLength(code) && code.equals(authDto.getCode())) {
            user.setPassword(bCryptPasswordEncoder.encode(authDto.getPassword()));
            user.setModifier(authDto.getUsername());
            userService.modify(user);
            final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(user.getEmail());
            AuthVo authVo = AuthVo.builder().token(jwtTokenUtil.generateToken(userDetails)).username(userDetails.getUsername()).authorities(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList())).build();
            return ResponseEntity.ok(authVo);
        }
        return ResponseEntity.status(Constant.EXCEPTION_NOT_FOUNT).build();
    }

    @PostMapping("/password")
    public ResponseEntity<User> changePwd(@RequestBody AuthDto authDto, Principal principal) {
        User user = userService.one(principal.getName());
        user.setPassword(bCryptPasswordEncoder.encode(authDto.getPassword()));
        user.setModifier(principal.getName());
        return ResponseEntity.ok(userService.modify(user));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthVo> me(Principal principal) {
        User user = userService.one(principal.getName());
        return ResponseEntity.ok(AuthVo.builder().username(user.getEmail()).name(user.getName()).tenant(user.getTenant().getName()).build());
    }

    private Map<String, Object> getMapFromIoJsonWebTokenClaims(DefaultClaims claims) {
        Map<String, Object> expectedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            expectedMap.put(entry.getKey(), entry.getValue());
        }
        return expectedMap;
    }

    private void authenticate(String username, String password) throws DisabledException, BadCredentialsException {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw e;
        } catch (BadCredentialsException e) {
            throw e;
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
