package com.bezkoder.spring.login.service;

import com.bezkoder.spring.login.models.Code;
import com.bezkoder.spring.login.repository.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CodeService {
    @Autowired
    public CodeRepository codeRepository;

    public void usedCode(String info){
        Code c = codeRepository.findByInfo(info)
                .orElseThrow(() -> new RuntimeException("code not found"));
        c.setUsed(true);
        codeRepository.save(c);
    }
    public Code getCodeByInfo(String info){
        return codeRepository.findByInfo(info)
                .orElseThrow(() -> new RuntimeException("code not found"));
    }

    public void saveCode(Code code){codeRepository.save(code);}
    public Stream<Code> getAllCodes(){
        return codeRepository.findAll().stream();
    }

    public Code initCode(){
        Code code = new Code();
        String info = getStringRandom(5);
        for(Code c: getAllCodes().collect(Collectors.toList())){
            if (Objects.equals(c.getInfo(), info)){
                info = getStringRandom(5);
            }
        }
        code.setInfo(info);
        return code;
    }
    public static String getStringRandom(int length) {

        StringBuilder val = new StringBuilder();
        Random random = new Random();
        for(int i = 0; i < length; i++) {
            int chatTypa = random.nextInt(3);
            switch (chatTypa){
                case 0:
                    // number
                    val.append(random.nextInt(10));
                    break;
                case 1:
                    // lowercase
                    val.append((char) (random.nextInt(26) + 97));
                    break;
                case 2:
                    // uppercase
                    val.append((char) (random.nextInt(26) + 65));
                    break;
            }
        }
        return val.toString();
    }

}
