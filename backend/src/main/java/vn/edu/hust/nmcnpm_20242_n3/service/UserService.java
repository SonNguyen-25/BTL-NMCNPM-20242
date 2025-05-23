package vn.edu.hust.nmcnpm_20242_n3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.edu.hust.nmcnpm_20242_n3.dto.FineDTO;
import vn.edu.hust.nmcnpm_20242_n3.dto.BookLoanDTO;
import vn.edu.hust.nmcnpm_20242_n3.dto.UserCreateDTO;
import vn.edu.hust.nmcnpm_20242_n3.dto.UserDTO;
import vn.edu.hust.nmcnpm_20242_n3.entity.BookLoan;
import vn.edu.hust.nmcnpm_20242_n3.entity.Fine;
import vn.edu.hust.nmcnpm_20242_n3.entity.Role;
import vn.edu.hust.nmcnpm_20242_n3.entity.User;
import vn.edu.hust.nmcnpm_20242_n3.constant.RoleEnum;
import vn.edu.hust.nmcnpm_20242_n3.repository.BookLoanRepository;
import vn.edu.hust.nmcnpm_20242_n3.repository.FineRepository;
import vn.edu.hust.nmcnpm_20242_n3.repository.RoleRepository;
import vn.edu.hust.nmcnpm_20242_n3.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    public UserDTO createUser(UserCreateDTO dto) throws IllegalArgumentException {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByUserName(dto.getUserName())) {
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());

        RoleEnum roleEnum = RoleEnum.valueOf(dto.getRoleName());
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Role not found: " + dto.getRoleName()));
        user.setRole(role);

        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }
    public Optional<UserDTO> getUserById(String id) {
        return userRepository.findById(id).map(this::mapToDTO);
    }

    public Optional<UserDTO> getUserByEmail(String email) {
        return userRepository.findByEmail(email).map(this::mapToDTO);
    }

    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUserName(username).map(this::mapToDTO);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream().map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUserName(user.getUserName());
        dto.setEmail(user.getEmail());
        dto.setRoleName(user.getRole().getName().name());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
    public UserDTO updateUser(String id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setName(dto.getName());
        user.setUserName(dto.getUserName());
        user.setEmail(dto.getEmail());

        RoleEnum roleEnum = RoleEnum.valueOf(dto.getRoleName());
        Role role = roleRepository.findByName(roleEnum)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        User updated = userRepository.save(user);
        return mapToDTO(updated);
    }
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }
    private BookLoanDTO toBookLoanDTO(BookLoan loan) {
        BookLoanDTO dto = new BookLoanDTO();
        dto.setId(loan.getId());
        dto.setLoanDate(loan.getLoanDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setActualReturnDate(loan.getActualReturnDate());
        dto.setStatus(loan.getStatus().toString());
        dto.setCurrentBookRequestId(loan.getCurrentBookRequestId());
        dto.setBookCopyId(loan.getBookCopy() != null ? loan.getBookCopy().getId() : null);
        return dto;
    }

    private FineDTO toFineDTO(Fine fine) {
        FineDTO dto = new FineDTO();
        dto.setId(fine.getId());
        dto.setAmount(fine.getAmount());
        dto.setCreatedAt(fine.getCreatedAt());
        dto.setUpdatedAt(fine.getUpdatedAt());
        dto.setBookLoanId(fine.getBookLoan() != null ? fine.getBookLoan().getId() : null);
        return dto;
    }
    @Autowired
    private BookLoanRepository bookLoanRepository;

    @Autowired
    private FineRepository fineRepository;

    public List<BookLoanDTO> getBookLoansByUserId(String userId) {
        List<BookLoan> loans = bookLoanRepository.findAllByUserId(userId);
        return loans.stream().map(this::toBookLoanDTO).collect(Collectors.toList());
    }

    public List<FineDTO> getFinesByUserId(String userId) {
        List<Fine> fines = fineRepository.findByUserId(userId);
        return fines.stream().map(this::toFineDTO).collect(Collectors.toList());
    }

}
