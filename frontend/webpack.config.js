const path = require("path");
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  mode: "none",
  entry: "./src/index.js",
  output: {
    path: __dirname + "/dist",
    filename: "bundle.js",
    publicPath: "/",
  },
  devtool: "eval-source-map",
  devServer: {
    contentBase: path.join(__dirname, "dist"),
    port: 3000,
    host: '0.0.0.0', // server to be accessible externally
    public: "localhost:3000", // force to open localhost instead of 0.0.0.0
    open: true, // open the default browser
    historyApiFallback: true, // serve index.html instead of routes leading to no specific ressource
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        pathRewrite: {'^/api': ''}
      },
    },
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: ["style-loader", "css-loader"],
      },

      // emits a separate file and exports the URLs => works for import in JS and url in CSS
      // default condition: a file with size less than 8kb will be treated as a inline module type and resource module type otherwise
      {
        test: /\.(png|jpg|gif|svg|mp3|mpe?g)$/,
        type: 'asset/resource',
      },

      {
        test: /\.html$/i,
        loader: 'html-loader',
      },
      {
        test: /\.js$/,
        exclude: /node_modules/,
        use: {
          loader: "babel-loader",
          options: {
            presets: [
              [
                "@babel/preset-env",
                {
                  targets: {
                    esmodules: true,
                  },
                },
              ],
            ],
          },
        },
      },
    ],
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: "./src/index.html",
      favicon: "./src/img/favicon.ico"
    })
  ]
};
