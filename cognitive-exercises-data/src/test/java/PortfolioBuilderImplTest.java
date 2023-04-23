import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.zelazobeton.cognitiveexercises.resource.application.ResourceService;
import com.zelazobeton.cognitiveexercises.user.domain.Portfolio;
import com.zelazobeton.cognitiveexercises.user.domain.PortfolioBuilder;
import com.zelazobeton.cognitiveexercises.user.domain.PortfolioBuilderImpl;
import com.zelazobeton.cognitiveexercises.user.domain.User;

public class PortfolioBuilderImplTest {

    private PortfolioBuilder sut;

    @Autowired
    private ResourceService resourceService;

    @Before
    public void setUp() {
        this.sut = new PortfolioBuilderImpl(this.resourceService);
    }

    @Test
    public void returnsBootstrapPortfolioWithGeneratedAvatar() throws IOException {
        // given
        User user = User.builder().username("testUsername").email("testUsername@mail.com").build();

        // when
        Portfolio portfolio = this.sut.createPortfolioWithGeneratedAvatar(user);

        // then
        assertThat(portfolio.getAvatar()).isNotNull();
        assertThat(portfolio.getUser()).isEqualTo(user);
        assertThat(portfolio.getTotalScore()).isEqualTo(0L);
    }
}
