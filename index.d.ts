declare module 'react-native-snap-kit'
export const SnapKit: SnapKitStatic
export default SnapKit
interface SnapKitStatic {
  sendSticker(
    assetName: string,
    url: string,
    width: number,
    height: number
  ): Promise<null>
}
