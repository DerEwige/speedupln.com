# Maximizing Earnings in the Lightning Network: Fee Potential, Relative Fee Potential, and Rebalancing

## 1. Fee Potential in the Lightning Network: A Comparison to Potential Energy

In the Lightning Network, nodes enable off-chain transactions by forwarding payments across various channels, charging fees for the service. A key element of this fee system is the "parts per million" (ppm) metric, which represents the fee charged per million satoshis transacted. Ignoring the base fee, the earning potential of a node in the Lightning Network can be viewed as a function of the fee rate (ppm) and the amount of satoshis it holds in its channels. This earning potential can be thought of as *fee potential*.

The concept of fee potential bears a resemblance to *potential energy* in physics, where the energy stored in an object is based on its position relative to other forces, such as gravity. Just as an object at a higher altitude has greater potential energy due to its position in a gravitational field, the satoshis locked in a channel on the Lightning Network have greater fee potential when the ppm is higher. 

### Key Components:
- **Satoshis as "Mass":** In this analogy, the amount of satoshis in a channel is similar to mass in the equation for potential energy. The more satoshis a node holds, the higher its potential to earn fees.
  
- **PPM as "Height":** The fee rate (ppm) is analogous to the height in potential energy. A higher fee rate per channel increases the node’s potential earnings, just as increasing an object’s height raises its potential energy.

Thus, a node's fee potential can be conceptualized as the product of the satoshis it holds and the ppm fee it charges, akin to potential energy being the product of mass, height, and gravitational force.

### Equation for Fee Potential:
The fee potential of a node can be simplified as: 

![image](https://github.com/user-attachments/assets/02c3bdb1-19fd-41d0-973b-9c9957d3218e)


This equation highlights that the more satoshis a node holds and the higher the ppm fee, the greater the potential earning from routing payments.

## 2. Relative Fee Potential: The Balance Between Fees and Routing

While setting a higher parts per million (ppm) fee rate increases the potential earnings in theory, in practice, the fee cannot be arbitrarily high. If a node sets its fee too high, it becomes unattractive for routing payments, as other channels with lower fees will be preferred. As a result, even though the *fee potential* would seem higher due to the increased fee rate, the actual earnings may drop because no transactions will flow through that channel. 

This introduces the concept of *relative fee potential*. Unlike pure fee potential, which only considers the fee rate and the amount of satoshis in a channel, relative fee potential takes into account the balance between having a fee that is neither too high nor too low. 

- **Too High a Fee (Overpriced):** If the fee rate is too high, nodes will avoid using that channel, leading to no routed transactions and zero earnings. In this case, the theoretical fee potential becomes irrelevant since no actual earnings are realized.
  
- **Too Low a Fee (Underpriced):** Conversely, setting the fee too low might attract a lot of traffic, but the earnings from each transaction will be minimal. This may result in the channel being used frequently but without generating substantial income for the node operator.

The key to optimizing earnings lies in setting the *optimal* fee, which strikes a balance between attracting sufficient routing traffic and charging enough to make each transaction profitable. The optimal fee will vary depending on the competition between nodes, the liquidity in the channel, and the network’s demand for routing through those specific nodes.

### Equation for Relative Fee Potential:
The relative fee potential between two nodes can be thought of as:

![image](https://github.com/user-attachments/assets/79b64473-9d3f-4b00-8a0f-7dc8dff8d4fa)


Here, the *Optimal ppm* is not simply the highest possible fee, but the fee that ensures a healthy balance of routing traffic while maintaining profitability. This is where the concept of competition and network dynamics comes into play: nodes must find the sweet spot where they remain competitive while maximizing earnings.

In essence, while the initial idea of fee potential suggests that more satoshis and a higher ppm fee will always lead to higher earnings, the reality is more nuanced. The relative fee potential—finding the optimal fee between too cheap and too expensive—is where true profitability lies.

## 3. Earning Through Rebalancing: Moving Satoshis to Maximize Relative Fee Potential

Rebalancing in the Lightning Network is a strategy where nodes move their satoshis between different channels to optimize their earning potential. The idea is to shift liquidity from channels with low relative fee potential (where earnings are suboptimal) to channels with higher relative fee potential, where the satoshis can generate more income. By doing this effectively, node operators can increase their overall earnings, provided that the cost of rebalancing is lower than the potential earnings gained from repositioning the liquidity.

### How Rebalancing Works

Each channel on a node has a different relative fee potential, determined by the balance of the satoshis in the channel and the optimal fee rate (ppm). If a channel is under-utilized (i.e., it charges a fee that is too low or has insufficient liquidity), the satoshis in that channel are not maximizing their earning potential. Meanwhile, a channel with a higher relative fee potential (more optimal fee and traffic) may need more liquidity to route larger payments or handle more transactions.

The key to earning money through rebalancing lies in moving satoshis from the low-performing channels to higher-performing channels. Here’s how it works:

1. **Identify Low-Performing Channels:** These are channels where the relative fee potential is low, meaning the channel either charges too little or has too much liquidity relative to the demand for routing through it. In this scenario, the satoshis in these channels aren’t generating significant earnings.

2. **Identify High-Performing Channels:** These are channels where the relative fee potential is higher. They have an optimal fee rate and see more demand for routing. However, they may be constrained by insufficient liquidity to route larger transactions.

3. **Move Satoshis (Rebalancing):** By transferring liquidity from low-performing to high-performing channels, you are effectively reallocating satoshis to places where they can generate more income. Rebalancing typically incurs a cost in the form of fees paid to other nodes along the rebalancing route.

4. **Net Gain from Rebalancing:** The profit from rebalancing occurs when the cost of moving the satoshis is lower than the additional earnings they will generate once repositioned in the higher relative fee potential channel. If you can move liquidity for a fee that is less than the difference in relative fee potential, you will have successfully earned money through rebalancing.

### Example of Rebalancing Profit

Assume a channel has 1 million satoshis but is in a low relative fee potential situation, earning 100 ppm (or 0.0001 satoshis per satoshi routed). On the other hand, another channel charges 500 ppm and has a much higher demand for routing, but only 100,000 satoshis are available for routing payments.

- **Current Potential Earnings (Low-Potential Channel):**

![image](https://github.com/user-attachments/assets/3b2d472c-276e-4ad9-9798-ba3c2d72f795)

  
- **Potential Earnings (High-Potential Channel):**

![image](https://github.com/user-attachments/assets/f27afc64-403d-48f2-9040-c143e511d35d)

If the cost of rebalancing (moving liquidity between the two channels) is 200 satoshis, then:

- **Net Gain from Rebalancing:**

![image](https://github.com/user-attachments/assets/824435b1-9ebe-462a-8835-f2bea5da6c96)

In this case, rebalancing allows you to move liquidity to a more profitable position, netting a higher earning potential while covering the cost of rebalancing.

### Why Rebalancing Can Be Profitable

- **Optimizing Liquidity Distribution:** Rebalancing helps distribute your satoshis across channels more effectively, ensuring that liquidity is where it is most needed and most profitable.
- **Arbitrage of Fee Potential:** By moving liquidity from low relative fee potential channels to higher ones, you are essentially performing arbitrage, capitalizing on the difference in earning potential across different parts of the network.
- **Cost-Efficiency:** The key to profitable rebalancing lies in the cost. If you can rebalance for less than the relative fee potential gain, you’re turning a profit.

In summary, rebalancing is about strategically shifting liquidity to channels with higher relative fee potential, allowing you to maximize your earnings. It’s not just about having more satoshis in a channel—it’s about ensuring that those satoshis are in the right place, where they can earn the most, while minimizing the costs of moving them.
