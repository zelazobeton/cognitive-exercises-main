import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.zelazobeton.cognitiveexercises.domain.Portfolio;
import com.zelazobeton.cognitiveexercises.domain.User;
import com.zelazobeton.cognitiveexercises.service.PortfolioBuilder;
import com.zelazobeton.cognitiveexercises.service.impl.PortfolioBuilderImpl;
import com.zelazobeton.cognitiveexercises.service.impl.ResourceServiceImpl;

public class PortfolioBuilderImplTest {

    private PortfolioBuilder sut;

    @Before
    public void setUp() {
        this.sut = new PortfolioBuilderImpl(new ResourceServiceImpl());
    }

    @Test
    public void returnsBootstrapPortfolioWithGeneratedAvatar() throws IOException {
        // given
        User user = User.builder().username("testUsername").email("testUsername@mail.com").build();

        // when
        Portfolio portfolio = this.sut.createBootstrapPortfolioWithGeneratedAvatar(user);

        // then
        assertThat(portfolio.getAvatar()).isNotNull();
        assertThat(portfolio.getUser()).isEqualTo(user);
        assertThat(portfolio.getTotalScore()).isEqualTo(0L);
    }
}
