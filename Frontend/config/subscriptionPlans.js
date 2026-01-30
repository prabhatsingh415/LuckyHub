const SUBSCRIPTION_PLANS = {
  FREE: {
    price: 0,
    maxGiveaways: 3,
    maxWinners: 2,
    maxComments: 300,
  },
  GOLD: {
    price: 49,
    maxGiveaways: 10,
    maxWinners: 5,
    maxComments: 600,
  },
  DIAMOND: {
    price: 79,
    maxGiveaways: -1,
    maxWinners: 10,
    maxComments: 1000,
  },
};

export default SUBSCRIPTION_PLANS;
