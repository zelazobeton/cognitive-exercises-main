import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.zelazobeton.cognitiveexercieses.domain.Portfolio;
import com.zelazobeton.cognitiveexercieses.domain.security.User;
import com.zelazobeton.cognitiveexercieses.service.PortfolioBuilder;
import com.zelazobeton.cognitiveexercieses.service.impl.PortfolioBuilderImpl;
import com.zelazobeton.cognitiveexercieses.service.impl.ResourceServiceImpl;

public class PortfolioBuilderImplTest {

    private PortfolioBuilder sut;

    @Before
    public void setUp() {
        sut = new PortfolioBuilderImpl(new ResourceServiceImpl());
    }

    @Test
    public void returnsBootstrapPortfolioWithGeneratedAvatar() throws IOException {
        // given
        User user = User.builder().username("testUsername").email("testUsername@mail.com").password("pass").build();

        // when
        Portfolio portfolio = this.sut.createBootstrapPortfolioWithGeneratedAvatar(user);

        // then
        assertThat(portfolio.getAvatar()).isNotNull();
        assertThat(portfolio.getUser()).isEqualTo(user);
        assertThat(portfolio.getTotalScore()).isEqualTo(0L);
    }
}
