package com.alibaba.cloud.nacos.configdata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.context.config.ConfigDataLocation;
import org.springframework.boot.context.config.ConfigDataLocationResolverContext;
import org.springframework.boot.context.config.Profiles;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.mock.env.MockEnvironment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * NacosConfigDataLocationResolver Tester.
 * 
 * @author freeman
 */
public class NacosConfigDataLocationResolverTest {

	private NacosConfigDataLocationResolver resolver;

	private ConfigDataLocationResolverContext context = mock(ConfigDataLocationResolverContext.class);

	private MockEnvironment environment;

	private Binder environmentBinder;

	@BeforeEach
	void setup() {
		this.environment = new MockEnvironment();
		this.environmentBinder = Binder.get(this.environment);
		this.resolver = new NacosConfigDataLocationResolver(new DeferredLog());
		when(context.getBinder()).thenReturn(environmentBinder);
	}

	@Test
	void testIsResolvable_givenIncorrectPrefix_thenReturnFalse() {
		assertThat(
				this.resolver.isResolvable(this.context, ConfigDataLocation.of("test:")))
						.isFalse();
	}

	@Test
	void testIsResolvable_givenCorrectPrefix_thenReturnTure() {
		assertThat(
				this.resolver.isResolvable(this.context, ConfigDataLocation.of("nacos:")))
						.isTrue();
		assertThat(this.resolver.isResolvable(this.context,
				ConfigDataLocation.of("optional:nacos:"))).isTrue();
	}

	@Test
	void testIsResolvable_givenDisable_thenReturnFalse() {
		this.environment.setProperty(NacosConfigProperties.PREFIX + ".enabled", "false");
		assertThat(
				this.resolver.isResolvable(this.context, ConfigDataLocation.of("nacos:")))
						.isFalse();
	}

	@Test
	void testResolveProfileSpecific_givenNothing_thenReturnDefaultProfile() {
		NacosConfigDataResource resource = testResolveProfileSpecific();
		assertThat(resource.getProfiles()).isEqualTo("default");
	}

	@Test
	void testStartWithASlashIsOK() {
		String locationUri = "nacos:/app";
		List<NacosConfigDataResource> resources = testUri(locationUri);
		assertThat(resources).hasSize(1);
		assertThat(resources.get(0).getConfig().getDataId()).isEqualTo("app");

		locationUri = "nacos:app";
		resources = testUri(locationUri);
		assertThat(resources).hasSize(1);
		assertThat(resources.get(0).getConfig().getDataId()).isEqualTo("app");
	}

	@Test
	void testDataIdMustBeSpecified() {
		String locationUri = "nacos:";
		assertThatThrownBy(() -> testUri(locationUri)).hasMessage("dataId must be specified");
	}

	@Test
	void testInvalidDataId() {
		String locationUri = "nacos:test/test.yml";
		assertThatThrownBy(() -> testUri(locationUri)).hasMessage("illegal dataId");
	}

	@Test
	void whenCustomizeSuffix_thenOverrideDefault() {
		String locationUri = "nacos:app";
		List<NacosConfigDataResource> resources = testUri(locationUri);
		assertThat(resources).hasSize(1);
		assertThat(resources.get(0).getConfig().getDataId()).isEqualTo("app");
		assertThat(resources.get(0).getConfig().getSuffix()).isEqualTo("properties");

		environment.setProperty("spring.cloud.nacos.config.file-extension", "yml");
		locationUri = "nacos:app";
		resources = testUri(locationUri);
		assertThat(resources).hasSize(1);
		assertThat(resources.get(0).getConfig().getDataId()).isEqualTo("app");
		assertThat(resources.get(0).getConfig().getSuffix()).isEqualTo("yml");

		locationUri = "nacos:app.json";
		resources = testUri(locationUri);
		assertThat(resources).hasSize(1);
		assertThat(resources.get(0).getConfig().getDataId()).isEqualTo("app.json");
		assertThat(resources.get(0).getConfig().getSuffix()).isEqualTo("json");
	}

	@Test
	void testUrisInLocationShouldOverridesProperty() {
		environment.setProperty("spring.cloud.nacos.config.group", "default");
		environment.setProperty("spring.cloud.nacos.config.refreshEnabled", "true");
		String locationUri = "nacos:test.yml?group=not_default&refreshEnabled=false";
		List<NacosConfigDataResource> resources = testUri(locationUri);
		assertThat(resources).hasSize(1);
		NacosConfigDataResource resource = resources.get(0);
		assertThat(resource.getConfig().getGroup()).isEqualTo("not_default");
		assertThat(resource.getConfig().getSuffix()).isEqualTo("yml");
		assertThat(resource.getConfig().isRefreshEnabled()).isFalse();
		assertThat(resource.getConfig().getDataId()).isEqualTo("test.yml");
	}

	private List<NacosConfigDataResource> testUri(String locationUri, String... activeProfiles) {
		when(context.getBootstrapContext())
				.thenReturn(mock(ConfigurableBootstrapContext.class));
		Profiles profiles = mock(Profiles.class);
		when(profiles.getActive()).thenReturn(Arrays.asList(activeProfiles));
		return this.resolver.resolveProfileSpecific(
				context, ConfigDataLocation.of(locationUri), profiles);
	}

	@Test
	void whenNoneInBootstrapContext_thenCreateNewConfigClientProperties() {
		ConfigurableBootstrapContext bootstrapContext = mock(
				ConfigurableBootstrapContext.class);
		when(context.getBootstrapContext()).thenReturn(bootstrapContext);
		when(bootstrapContext.isRegistered(eq(NacosConfigProperties.class)))
				.thenReturn(false);
		when(bootstrapContext.get(eq(NacosConfigProperties.class)))
				.thenReturn(new NacosConfigProperties());
		List<NacosConfigDataResource> resources = this.resolver.resolveProfileSpecific(
				context, ConfigDataLocation.of("nacos:test.yml"),
				mock(Profiles.class));
		assertThat(resources).hasSize(1);
		verify(bootstrapContext, times(0)).get(eq(NacosConfigProperties.class));
		NacosConfigDataResource resource = resources.get(0);
		assertThat(resource.getConfig().getGroup()).isEqualTo("DEFAULT_GROUP");
		assertThat(resource.getConfig().getDataId()).isEqualTo("test.yml");
	}

	private NacosConfigDataResource testResolveProfileSpecific() {
		return testResolveProfileSpecific("default");
	}

	private NacosConfigDataResource testResolveProfileSpecific(String activeProfile) {
		when(context.getBootstrapContext())
				.thenReturn(mock(ConfigurableBootstrapContext.class));
		Profiles profiles = mock(Profiles.class);
		if (activeProfile != null) {
			when(profiles.getActive())
					.thenReturn(Collections.singletonList(activeProfile));
			when(profiles.getAccepted())
					.thenReturn(Collections.singletonList(activeProfile));
		}

		List<NacosConfigDataResource> resources = this.resolver.resolveProfileSpecific(
				context, ConfigDataLocation.of("nacos:test.yml"), profiles);
		assertThat(resources).hasSize(1);
		return resources.get(0);
	}

}
