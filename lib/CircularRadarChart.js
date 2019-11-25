import PropTypes from 'prop-types';
import React, {Component} from 'react';
import {
  requireNativeComponent,
  View
} from 'react-native';

import PieRadarChartBase from './PieRadarChartBase';
import {yAxisIface} from './AxisIface';
import {radarData} from './ChartDataConfig';

class CircularRadarChart extends React.Component {
  getNativeComponentName() {
    return 'RNCircularRadarChart'
  }

  getNativeComponentRef() {
    return this.nativeComponentRef
  }

  render() {
    return <RNCircularRadarChart {...this.props} ref={ref => this.nativeComponentRef = ref} />;
  }
}

CircularRadarChart.propTypes = {
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

var RNCircularRadarChart = requireNativeComponent('RNCircularRadarChart', CircularRadarChart, {
  nativeOnly: {onSelect: true, onChange: true}
});

export default CircularRadarChart