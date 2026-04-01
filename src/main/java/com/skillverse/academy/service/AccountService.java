package com.skillverse.academy.service;

import com.skillverse.academy.dto.ParticipantLoginForm;
import com.skillverse.academy.dto.ParticipantRegisterForm;
import com.skillverse.academy.model.Account;
import com.skillverse.academy.model.AccountRole;
import com.skillverse.academy.repository.AccountRepository;
import java.util.Locale;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account registerParticipant(ParticipantRegisterForm form) {
        String email = normalizeEmail(form.getEmail());
        if (accountRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        Account account = new Account();
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        account.setRole(AccountRole.PARTICIPANT);
        account.setParticipantType(form.getParticipantType());
        account.setActive(true);
        return accountRepository.save(account);
    }

    public Account authenticateParticipant(ParticipantLoginForm form) {
        Account account = accountRepository.findByEmailIgnoreCase(normalizeEmail(form.getEmail()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        if (account.getRole() != AccountRole.PARTICIPANT
                || account.getParticipantType() != form.getParticipantType()
                || !account.isActive()
                || !passwordEncoder.matches(form.getPassword(), account.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return account;
    }

    public boolean participantAccountExists(String email) {
        return accountRepository.existsByEmailIgnoreCase(normalizeEmail(email));
    }

    public Account findParticipantAccount(String email) {
        return accountRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .filter(account -> account.getRole() == AccountRole.PARTICIPANT)
                .orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmailIgnoreCase(normalizeEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("Account not found."));

        if (account.getRole() != AccountRole.ADMIN || !account.isActive()) {
            throw new UsernameNotFoundException("Admin account not found.");
        }

        return User.withUsername(account.getEmail())
                .password(account.getPasswordHash())
                .roles(account.getRole().name())
                .build();
    }

    public String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
    }
}
