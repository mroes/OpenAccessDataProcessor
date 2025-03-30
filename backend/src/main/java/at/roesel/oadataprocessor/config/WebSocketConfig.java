/*
 *  Copyright (c) 2025 Dr. Martin Rösel <opensource@roesel.at>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package at.roesel.oadataprocessor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // https://stackoverflow.com/questions/56065309/attribute-servercontainer-not-found-in-servletcontext
    private boolean ignoreNullWsContainer;

    @Value("${security.allowedOrigins:*}")
    private String allowedOrigins;

    // set false for tests to avoid the exception
    // "A ServletContext is required to access the javax.websocket.server.ServerContainer instance"
    @Value("${test.ignore-null-websocket-container:false}")
    private void setIgnoreNullWsContainer(String flag) {
        this.ignoreNullWsContainer = Boolean.parseBoolean(flag);
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Channel on the WebSocket client
        config.enableSimpleBroker("/topic", "/queue");
        // Channel on the WebSocket server
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websock")
                .setAllowedOriginPatterns(allowedOrigins)
                .setHandshakeHandler(new CustomHandshakeHandler())
                .withSockJS();
    }

    @Bean
    public ServletServerContainerFactoryBean createServletServerContainerFactoryBean() {

        if (ignoreNullWsContainer) {
            return null;
        }

        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(128 * 10124);
        container.setMaxBinaryMessageBufferSize(128 * 1024);
        return container;
    }
}
