package com.example.SpringBootMisson1.question;

import com.example.SpringBootMisson1.DataNotFoundException;
import com.example.SpringBootMisson1.answer.Answer;
import com.example.SpringBootMisson1.user.SiteUser;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }

    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
        Specification<Question> spec = search(kw);
        return this.questionRepository.findAllByKeyword(kw, pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser) {
        question.getVoter().add(siteUser);
        this.questionRepository.save(question);
    }

    public void views(Integer id) {
        Question question = getQuestion(id);
        question.setViews(question.getViews() + 1);
        questionRepository.save(question);
    }

    public boolean hasUserViewed(Integer id, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("viewedQuestions")) {
                    String cookieValue = cookie.getValue();
                    if (cookieValue != null && !cookieValue.isEmpty()) {
                        try {
                            String[] viewedIds = URLDecoder.decode(cookieValue, "UTF-8").split(",");
                            Set<Integer> viewedSet = new HashSet<>();
                            for (String viewedId : viewedIds) {
                                if (!viewedId.isEmpty() && viewedId.matches("\\d+")) { // 숫자만 체크
                                    viewedSet.add(Integer.parseInt(viewedId));
                                }
                            }
                            return viewedSet.contains(id);
                        } catch (Exception e) {
                            System.err.println("Error parsing viewed IDs: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return false;
    }

    public void markQuestionAsViewed(Integer id, HttpServletResponse response, HttpServletRequest request) {
        try {
            // 쿠키에서 현재 본 질문 ID 목록을 가져옴
            String viewedQuestions = "";
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("viewedQuestions")) {
                        viewedQuestions = cookie.getValue();
                        break; // 쿠키 찾으면 바로 탈출
                    }
                }
            }

            // 본 질문 ID 목록에 새로운 ID 추가
            Set<Integer> viewedSet = new HashSet<>();
            if (!viewedQuestions.isEmpty()) {
                for (String viewedId : viewedQuestions.split(",")) {
                    if (viewedId.matches("\\d+")) { // 숫자만 체크
                        viewedSet.add(Integer.parseInt(viewedId));
                    }
                }
            }
            viewedSet.add(id);
            String updatedViewedQuestions = String.join(",", viewedSet.stream().map(String::valueOf).toArray(String[]::new));

            // 쿠키에 저장
            Cookie cookie = new Cookie("viewedQuestions", URLEncoder.encode(updatedViewedQuestions, "UTF-8"));
            cookie.setMaxAge(60 * 60 * 24); // 1일 동안 유효
            cookie.setPath("/"); // 모든 경로에서 유효
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            System.err.println("Error encoding cookie value: " + e.getMessage());
            e.printStackTrace(); // 예외 처리
        }
    }
}