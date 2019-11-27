import PropTypes from 'prop-types';
import React, {Component} from 'react';
import {
  requireNativeComponent,
  View
} from 'react-native';

import PieRadarChartBase from './PieRadarChartBase';
import {yAxisIface} from './AxisIface';
import {radarData} from './ChartDataConfig';
//
class CircularRadarArcChart extends React.Component {
  getNativeComponentName() {
    return 'RNCircularRadarArcChart'
  }

  getNativeComponentRef() {
    return this.nativeComponentRef
  }

  render() {
    return <RNCircularRadarArcChart {...this.props} ref={ref => this.nativeComponentRef = ref} />;
  }
}

CircularRadarArcChart.propTypes = {
  ...PieRadarChartBase.propTypes,

  yAxis: PropTypes.shape(yAxisIface),

  drawWeb: PropTypes.bool,
  skipWebLineCount: PropTypes.number,

  webLineWidth: PropTypes.number,
  webLineWidthInner: PropTypes.number,
  webAlpha: PropTypes.number,
  webColor: PropTypes.number,
  webColorInner: PropTypes.number,

  data: radarData
};

var RNCircularRadarArcChart = requireNativeComponent('RNCircularRadarArcChart', CircularRadarArcChart, {
  nativeOnly: {onSelect: true, onChange: true}
});

export default CircularRadarArcChart