package renewal.awesome_travel_backoffice.user;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import renewal.common.entity.MemberGradeRule;
import renewal.common.entity.User.MemberGrade;
import renewal.common.repository.MemberGradeRuleRepository;

@Component
@RequiredArgsConstructor
public class DefaultMemberGradeRuleLoader implements ApplicationRunner {

    // 멤버 등급기준 초기설정
    private final MemberGradeRuleRepository memberGradeRuleRepo;

    @Override
    public void run(ApplicationArguments args) {
        if (memberGradeRuleRepo.count() > 0)
            return;

        memberGradeRuleRepo.save(new MemberGradeRule(null, MemberGrade.BASIC, 0, 0, 0, 0, 1));
        memberGradeRuleRepo.save(new MemberGradeRule(null, MemberGrade.GREEN, 3, 10, 0, 1000000, 2));
        memberGradeRuleRepo.save(new MemberGradeRule(null, MemberGrade.BLUE, 5, 15, 200000, 2000000, 3));
        memberGradeRuleRepo.save(new MemberGradeRule(null, MemberGrade.PURPLE, 8, 20, 300000, 3000000, 4));
        memberGradeRuleRepo.save(new MemberGradeRule(null, MemberGrade.BLACK, 10, 30, 500000, 5000000, 5));
    }
}
