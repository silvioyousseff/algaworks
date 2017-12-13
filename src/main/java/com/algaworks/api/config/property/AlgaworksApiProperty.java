package com.algaworks.api.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("algaworks")
public class AlgaworksApiProperty {

	private final Seguranca seguranca = new Seguranca();

	public Seguranca getSeguranca() {
		return seguranca;
	}

	public static class Seguranca {
		private boolean enableHttps;

		public boolean isEnableHttps() {
			return enableHttps;
		}

		public void setEnableHttps(boolean enableHttps) {
			this.enableHttps = enableHttps;
		}
	}
}
