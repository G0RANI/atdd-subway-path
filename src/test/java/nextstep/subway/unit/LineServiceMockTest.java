package nextstep.subway.unit;

import nextstep.subway.applicaion.LineService;
import nextstep.subway.applicaion.StationService;
import nextstep.subway.applicaion.dto.SectionRequest;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LineServiceMockTest {
    @Mock
    private LineRepository lineRepository;
    @Mock
    private StationService stationService;

    @Test
    @DisplayName("구간 추가")
    void addSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line 이호선 = createLineWithMock(new Line(1L, "2호선", "green"));
        Station 삼성역 = createStationWithMock(new Station(1L, "삼성역"));
        Station 역삼역 = createStationWithMock(new Station(2L, "역삼역"));

        LineService lineService = new LineService(lineRepository, stationService);

        // when
        // lineService.addSection 호출
        addSection(lineService, 이호선, 삼성역, 역삼역);

        // then
        // line.findLineById 메서드를 통해 검증
        assertAll(() -> assertThat(이호선.getSections().size()).isEqualTo(1)
                , () -> assertThat(이호선.getAllStations().stream().map(Station::getName))
                        .containsOnly("삼성역", "역삼역")
        );
    }


    @Test
    @DisplayName("구간 삭제")
    void deleteSection() {
        // given
        // lineRepository, stationService stub 설정을 통해 초기값 셋팅
        Line 이호선 = createLineWithMock(new Line(1L, "2호선", "green"));

        Station 삼성역 = createStationWithMock(new Station(1L, "삼성역"));
        Station 선릉역 = createStationWithMock(new Station(2L, "선릉역"));
        Station 역삼역 = createStationWithMock(new Station(3L, "역삼역"));

        LineService lineService = new LineService(lineRepository, stationService);

        addSection(lineService, 이호선, 삼성역, 선릉역);
        addSection(lineService, 이호선, 선릉역, 역삼역);

        // when
        // lineService.deleteSection 호출
        lineService.deleteSection(이호선.getId(), 역삼역.getId());

        // then
        // line.getSections 메서드를 통해 검증
        assertAll(() -> assertThat(이호선.getSections().size()).isEqualTo(1)
                , () -> assertThat(이호선.getAllStations().stream().map(Station::getName))
                        .containsOnly("삼성역", "선릉역")
        );
    }

    private static void addSection(LineService lineService, Line line, Station upStation, Station downStation) {
        SectionRequest sectionRequest = new SectionRequest(upStation.getId(), downStation.getId(), 10);
        lineService.addSection(line.getId(), sectionRequest);
    }

    private Station createStationWithMock(Station station) {
        when(stationService.findById(station.getId())).thenReturn(station);
        return station;
    }

    private Line createLineWithMock(Line line) {
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));
        return line;
    }
}
