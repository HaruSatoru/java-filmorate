package ru.yandex.practicum.filmorate.controllers;

class UserControllerTest {

//    private Validator validator;
//    private UserController userController;
//
//    @BeforeEach
//    void setup() {
//        userController = new UserController();
//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        validator = factory.getValidator();
//    }
//
//    @Test
//    void createUserWithNullNameEqualsLogin() throws ValidationException {
//        LocalDate localDate = LocalDate.of(2000, 12, 12);
//        User user = new User(1, "maximus123", null, "maximus@example.com", localDate);
//
//        User addedUser = userController.addUser(user);
//
//        assertEquals(addedUser.getName(), user.getLogin());
//    }
//
//
//    @Test
//    void createUserWithIncorrectEmail() {
//        LocalDate localDate = LocalDate.of(2000, 12, 12);
//        User invalidEmailUser = new User(1, "maximus123", "Maximus", "maximusexample.com", localDate);
//        User nullEmailUser = new User(1, "maximus123", "Maximus", null, localDate);
//
//        Set<ConstraintViolation<User>> violations = validator.validate(invalidEmailUser);
//        Set<ConstraintViolation<User>> violations1 = validator.validate(nullEmailUser);
//
//        assertFalse(violations.isEmpty());
//        assertFalse(violations1.isEmpty());
//    }
//
//    @Test
//    void createUserWithFutureBirthday() {
//        LocalDate futureBirthday = LocalDate.of(3000, 12, 12);
//        User user = new User(1, "maximus123", "Maximus", "maximus@example.com", futureBirthday);
//
//        Set<ConstraintViolation<User>> violations = validator.validate(user);
//
//        assertFalse(violations.isEmpty());
//    }
}