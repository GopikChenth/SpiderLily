package com.arcadelabs.spiderlily.mihon.parsers.config

interface ContentSourceConfig {
	operator fun <T> get(key: ConfigKey<T>): T
}
