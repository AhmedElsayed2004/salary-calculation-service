package com.gold.salarycalculation;

import com.gold.salarycalculation.service.util.CalculationDateResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculationDateResolverTest {
    @Mock
    private Clock clock;

    @InjectMocks
    private CalculationDateResolver calculationDateResolver;

    @Test
    public void resolve_ShouldReturnTheCurrentDate_WhenCurrentMonth() {
        // Arrange
        String monthKey = "2022-02";

        when(clock.instant()).thenReturn(Instant.parse("2022-02-17T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        LocalDate expectedDate = LocalDate.parse("2022-02-17");

        // Act
        LocalDate result = calculationDateResolver.resolve(monthKey);

        // Assert
        assertThat(result).isEqualTo(expectedDate);

    }

    @Test
    public void resolve_ShouldReturnTheEndOfMonth_WhenPastMonth() {
        // Arrange
        String monthKey = "2022-01";

        when(clock.instant()).thenReturn(Instant.parse("2022-02-17T00:00:00Z"));
        when(clock.getZone()).thenReturn(ZoneId.of("UTC"));

        LocalDate expectedDate = LocalDate.parse("2022-01-31");

        // Act
        LocalDate result = calculationDateResolver.resolve(monthKey);

        // Assert
        assertThat(result).isEqualTo(expectedDate);

    }
}
